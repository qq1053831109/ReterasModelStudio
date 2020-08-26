package com.hiveworkshop.rms.editor.render3d;

import com.hiveworkshop.rms.editor.model.ParticleEmitter2;
import com.hiveworkshop.rms.ui.application.viewer.AnimatedRenderEnvironment;
import com.hiveworkshop.rms.util.MathUtils;
import com.hiveworkshop.rms.util.Matrix4;
import com.hiveworkshop.rms.util.QuaternionRotation;
import com.hiveworkshop.rms.util.Vertex3;
import com.hiveworkshop.rms.util.Vertex4;

public class RenderParticle2 extends EmittedObject<RenderParticleEmitter2View> {
	private static final Vertex4 vector4Heap = new Vertex4();
	private static final Vertex4 colorHeap = new Vertex4();
	private static final Vertex4 color1Heap = new Vertex4();
	private static final Vertex4 color2Heap = new Vertex4();
	private static final QuaternionRotation rotationZHeap = new QuaternionRotation();
	private static final QuaternionRotation rotationYHeap = new QuaternionRotation();
	private static final QuaternionRotation rotationXHeap = new QuaternionRotation();
	private static final Matrix4 matrixHeap = new Matrix4();
	private static final Vertex3 locationHeap = new Vertex3();
	private static final Vertex4 location4Heap = new Vertex4();
	private static final Vertex4 startHeap = new Vertex4();
	private static final Vertex4 endHeap = new Vertex4();
	private static final Vertex3 tailHeap = new Vertex3();
	private static final Vertex3 normalHeap = new Vertex3();
	private final RenderParticleEmitter2 emitter;
	private boolean head;
	private final Vertex3 location;
	private final Vertex3 velocity;
	private float gravity;
	private final Vertex3 nodeScale;

	private RenderNode node;

	public RenderParticle2(final RenderParticleEmitter2 emitter) {
		this.emitter = emitter;
		emitterView = null;
		health = 0;
		head = true;
		location = new Vertex3();
		velocity = new Vertex3();
		gravity = 0;
		nodeScale = new Vertex3();

		vertices = new float[12];
		lta = 0;
		lba = 0;
		rta = 0;
		rba = 0;
		rgb = 0;
	}

	@Override
	public void reset(final RenderParticleEmitter2View emitterView, final boolean isHead) {
		double width = emitterView.getLength();
		double length = emitterView.getWidth();
		double latitude = emitterView.getLatitude();
		final double variation = emitterView.getVariation();
		final double speed = emitterView.getSpeed();
		final double gravity = emitterView.getGravity();

		final ParticleEmitter2 modelObject = emitter.modelObject;
		final RenderNode node = emitterView.instance.getRenderNode(modelObject);
		final Vertex3 pivotPoint = modelObject.getPivotPoint();
		final Vertex3 scale = node.getWorldScale();
		width *= 0.5;
		length *= 0.5;
		latitude = Math.toRadians(latitude);

		this.emitterView = emitterView;
		this.node = node;
		health = (float) modelObject.getLifeSpan();
		head = isHead;
		this.gravity = (float) (gravity * scale.z);

		nodeScale.set(scale);

		// Local location
		location.x = (float) (pivotPoint.x + MathUtils.randomInRange(-width, width));
		location.y = (float) (pivotPoint.y + MathUtils.randomInRange(-length, length));
		location.z = (float) (pivotPoint.z);

		// World location
		if (!modelObject.getModelSpace()) {
			vector4Heap.set(location.x, location.y, location.z, 1);
			Matrix4.transform(node.getWorldMatrix(), vector4Heap, vector4Heap);
			location.set(vector4Heap);
		}

		// Location rotation
		rotationZHeap.setIdentity();
		vector4Heap.set(0, 0, 1, (float) (Math.PI / 2));
		rotationZHeap.setFromAxisAngle(vector4Heap);
		vector4Heap.set(1, 0, 0, MathUtils.randomInRange(-latitude, latitude));
		rotationYHeap.setFromAxisAngle(vector4Heap);
		QuaternionRotation.mul(rotationYHeap, rotationZHeap, rotationYHeap);

		// If this is not a line emitter, emit in a sphere rather than a circle
		if (!modelObject.getLineEmitter()) {
			vector4Heap.set(0, 1, 0, MathUtils.randomInRange(-latitude, latitude));
			rotationXHeap.setFromAxisAngle(vector4Heap);
			QuaternionRotation.mul(rotationXHeap, rotationYHeap, rotationYHeap);
		}

		// World rotation
		if (!modelObject.getModelSpace()) {
			QuaternionRotation.mul(node.getWorldRotation(), rotationYHeap, rotationYHeap);
		}

		// Apply the rotation
		MathUtils.fromQuat(rotationYHeap, matrixHeap);
		vector4Heap.set(0, 0, 1, 1);
		Matrix4.transform(matrixHeap, vector4Heap, vector4Heap);
		velocity.set(vector4Heap);

		// Apply speed
		velocity.scale((float) speed + MathUtils.randomInRange(-variation, variation));

		// Apply the parent's scale
		velocity.x *= scale.x;
		velocity.y *= scale.y;
		velocity.z *= scale.z;
	}

	@Override
	public void update() {
		final ParticleEmitter2 modelObject = emitter.modelObject;
		final float dt = AnimatedRenderEnvironment.FRAMES_PER_UPDATE * 0.001f;
		final Vertex3 worldLocation = locationHeap;
		final Vertex4 worldLocation4f = location4Heap;

		health -= dt;

		velocity.z -= gravity * dt;

		location.x = location.x + (velocity.x * dt);
		location.y = location.y + (velocity.y * dt);
		location.z = location.z + (velocity.z * dt);

		worldLocation.set(location);
		worldLocation4f.set(location.x, location.y, location.z, 1);

		final float lifeFactor = (float) ((modelObject.getLifeSpan() - health) / modelObject.getLifeSpan());
		final float timeMiddle = (float) modelObject.getTime();
		float factor;
		final int firstColor;
		final Vertex3 interval;

		if (lifeFactor < timeMiddle) {
			factor = lifeFactor / timeMiddle;

			firstColor = 0;

			if (head) {
				interval = modelObject.getHeadUVAnim();
			} else {
				interval = modelObject.getTailUVAnim();
			}
		} else {
			factor = (lifeFactor - timeMiddle) / (1 - timeMiddle);

			firstColor = 1;

			if (head) {
				interval = modelObject.getHeadDecayUVAnim();
			} else {
				interval = modelObject.getTailDecayUVAnim();
			}
		}

		factor = Math.min(factor, 1);

		final float start = (float) interval.x;
		final float end = (float) interval.y;
		final float repeat = (float) interval.z;
		final Vertex3 scaling = modelObject.getParticleScaling();
		final Vertex3[] colors = modelObject.getSegmentColors();
		final float scale = (float) MathUtils.lerp((float) scaling.getCoord((byte) firstColor),
				(float) scaling.getCoord((byte) (firstColor + 1)), factor);
		final float left;
		final float top;
		final float right;
		final float bottom;
		final RenderModel instance = emitterView.instance;

		// If this is a team colored emitter, get the team color tile from the atlas
		// Otherwise do normal texture atlas handling.
		if (modelObject.isTeamColored()) {
			// except that Matrix Eater has no such atlas and we are simply copying from
			// Ghostwolf
			left = 0;
			top = 0;
			right = left + 1;
			bottom = top + 1;
		} else {
			final int columns = modelObject.getCols();
			float index = 0;
			final float spriteCount = end - start;
			if ((spriteCount > 0) && ((columns > 1) || (modelObject.getRows() > 1))) {
				// Repeating speeds up the sprite animation, which makes it effectively run N
				// times in its interval.
				// E.g. if repeat is 4, the sprite animation will be seen 4 times, and thus also
				// run 4 times as fast
				index = (float) (start + (Math.floor(spriteCount * repeat * factor) % spriteCount));
			}

			left = index % columns;
			top = (int) (index / columns);
			right = left + 1;
			bottom = top + 1;
		}

		final Vertex3 firstColorVertexME = colors[firstColor];
		final Vertex3 secondColorVertexME = colors[firstColor + 1];
		color1Heap.set((float) firstColorVertexME.x, (float) firstColorVertexME.y, (float) firstColorVertexME.z,
				(float) modelObject.getAlpha().getCoord((byte) firstColor));
		color2Heap.set((float) secondColorVertexME.x, (float) secondColorVertexME.y, (float) secondColorVertexME.z,
				(float) modelObject.getAlpha().getCoord((byte) (firstColor + 1)));
		MathUtils.lerp(colorHeap, color1Heap, color2Heap, factor);

		final int a = ((int) colorHeap.w) & 0xFF;

		lta = MathUtils.uint8ToUint24((byte) right, (byte) bottom, (byte) a);
		lba = MathUtils.uint8ToUint24((byte) left, (byte) bottom, (byte) a);
		rta = MathUtils.uint8ToUint24((byte) right, (byte) top, (byte) a);
		rba = MathUtils.uint8ToUint24((byte) left, (byte) top, (byte) a);
		rgb = MathUtils.uint8ToUint24((byte) ((int) (colorHeap.z * 255) & 0xFF),
				(byte) ((int) (colorHeap.y * 255) & 0xFF), (byte) ((int) (colorHeap.x * 255) & 0xFF));

		final Vertex4[] vectors;

		// Choose between a default rectangle or a billboarded one
		if (modelObject.getXYQuad()) {
			vectors = instance.getSpacialVectors();
		} else {
			vectors = instance.getBillboardVectors();
		}

		final float[] vertices = this.vertices;
		final Vertex3 nodeScale = this.nodeScale;

		final float scalex = scale * nodeScale.x;
		final float scaley = scale * nodeScale.y;
		final float scalez = scale * nodeScale.z;

		if (head) {
			// If this is a model space emitter, the particle location is in local space, so
			// convert it now to world space.
			if (modelObject.getModelSpace()) {
				Matrix4.transform(node.getWorldMatrix(), worldLocation4f, worldLocation4f);
			}

			final float px = worldLocation4f.x;
			final float py = worldLocation4f.y;
			final float pz = worldLocation4f.z;

			final Vertex4 pv1 = vectors[0];
			final Vertex4 pv2 = vectors[1];
			final Vertex4 pv3 = vectors[2];
			final Vertex4 pv4 = vectors[3];

			vertices[0] = px + (pv1.x * scalex);
			vertices[1] = py + (pv1.y * scaley);
			vertices[2] = pz + (pv1.z * scalez);
			vertices[3] = px + (pv2.x * scalex);
			vertices[4] = py + (pv2.y * scaley);
			vertices[5] = pz + (pv2.z * scalez);
			vertices[6] = px + (pv3.x * scalex);
			vertices[7] = py + (pv3.y * scaley);
			vertices[8] = pz + (pv3.z * scalez);
			vertices[9] = px + (pv4.x * scalex);
			vertices[10] = py + (pv4.y * scaley);
			vertices[11] = pz + (pv4.z * scalez);
		} else {
			final double tailLength = modelObject.getTailLength();
			final double offsetx = tailLength * velocity.x * 1;
			final double offsety = tailLength * velocity.y * 1;
			final double offsetz = tailLength * velocity.z * 1;

			// The start and end of the tail
			startHeap.set((float) (worldLocation4f.x - offsetx), (float) (worldLocation4f.y - offsety),
					(float) (worldLocation4f.z - offsetz), 1);
			endHeap.set((worldLocation4f.x), (worldLocation4f.y), (worldLocation4f.z), 1);

			// If this is a model space emitter, the start and end are in local space, so
			// convert them to world space.
			if (modelObject.getModelSpace()) {
				Matrix4.transform(node.getWorldMatrix(), startHeap, startHeap);
				Matrix4.transform(node.getWorldMatrix(), endHeap, endHeap);
			}

			final float startx = startHeap.x;
			final float starty = startHeap.y;
			final float startz = startHeap.z;
			final float endx = endHeap.x;
			final float endy = endHeap.y;
			final float endz = endHeap.z;

			// Get the normal to the tail in camera space
			// This allows to build a 2D rectangle around the 3D tail
			tailHeap.set(endx - startx, endy - starty, endz - startz);
			if (tailHeap.lengthSquared() > 0) {
				tailHeap.normalize();
			}
			normalHeap.set(instance.getBillboardVectors()[6]);
			Vertex3.cross(normalHeap, tailHeap, normalHeap);
			if (normalHeap.lengthSquared() > 0) {
				normalHeap.normalize();
			}

			final float normalX = normalHeap.x * scalex;
			final float normalY = normalHeap.y * scaley;
			final float normalZ = normalHeap.z * scalez;

			vertices[0] = startx - normalX;
			vertices[1] = starty - normalY;
			vertices[2] = startz - normalZ;

			vertices[6] = endx + normalX;
			vertices[7] = endy + normalY;
			vertices[8] = endz + normalZ;

			vertices[3] = endx - normalX;
			vertices[4] = endy - normalY;
			vertices[5] = endz - normalZ;

			vertices[9] = startx + normalX;
			vertices[10] = starty + normalY;
			vertices[11] = startz + normalZ;
		}
	}
}