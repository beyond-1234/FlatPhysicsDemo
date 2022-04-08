package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;


public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Texture texture;
	Pixmap pixmap;
	ShapeDrawer drawer;
	float strokeWidth;
	int count = 10;

	float totalHeight;
	float totalWidth;

	ArrayList<FlatBody> bodyList;
	ArrayList<Color> colorList;
	ArrayList<Color> outlineColorList;

	// cache stuff instead of creating new object everytime
	FlatVector cachedDirection;
	float[] cachedVertices;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);
		totalHeight = camera.viewportHeight;
		totalWidth  = camera.viewportWidth;

		initializeDrawer();

		initializeRandomList();

//		initializeCircleList();

//		initializeBoxList();
	}


	@Override
	public void render () {
		ScreenUtils.clear(0, 0.3f, 0.3f, 1);
		batch.begin();
		camera.update();

		reset();

		float deltaX = 0f;
		float deltaY = 0f;
		float speed  = 8f;

		move(deltaX, deltaY, speed);

		collide();

		drawList();
//		drawCircleList();

//		boxMove(deltaX, deltaY, speed);
//
//		boxCollide();
//
//		drawBoxList();

		batch.end();
	}

	private void reset() {
		for (int i = 0; i < count; i++) {
			outlineColorList.get(i).set((Color.WHITE));
		}
	}


	@Override
	public void dispose () {
		batch.dispose();
		pixmap.dispose();
		texture.dispose();
	}

	private void initializeDrawer() {
		pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		texture = new Texture(pixmap); //remember to dispose of later
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
		drawer = new ShapeDrawer(batch, region);
		strokeWidth = drawer.getDefaultLineWidth();
	}

	private void initializeList() {
		cachedDirection = new FlatVector(0f, 0f);
		colorList = new ArrayList<>();
		outlineColorList = new ArrayList<>();
		bodyList = new ArrayList<>();
	}

	private void initializeRandomList() {
		initializeList();

		for (int i = 0; i < count; i++) {
			FlatVector center = new FlatVector( (float) Math.random() * totalWidth, (float) Math.random() * totalHeight);
			if (Math.random() > 0.5) {
				bodyList.add(FlatBody.createCircleBody(20f, center, 2f, false, 0.5f));
			}else {
				bodyList.add(FlatBody.createBoxBody(40f, 40f, center, 2f, false, 0.5f));
			}
//			System.out.println(bodyList.get(i));
			outlineColorList.add(new Color(Color.WHITE));
			colorList.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
		}
	}

	private void move(float deltaX, float deltaY, float speed) {
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) 	deltaX--;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) 	deltaX++;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) 	deltaY--;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) 		deltaY++;

		if(deltaX != 0f || deltaY != 0f) {
			cachedDirection.setFlatVector(deltaX, deltaY);
			FlatVector direction = FlatMath.normalize(cachedDirection);
			FlatVector velocity  = FlatVector.multiply(direction, speed);
			bodyList.get(0).move(velocity);
		}
	}

	private void collide() {
		for (int i = 0; i < this.bodyList.size() - 1; i++) {

			FlatBody bodyA = this.bodyList.get(i);

			for (int j = i + 1; j < this.bodyList.size(); j++) {
				FlatBody bodyB = this.bodyList.get(j);
				Collisions.CollisionResult collisionResult;

				if(bodyA.getShapeType() == bodyB.getShapeType()) {
					if(FlatBody.BOX_SHAPE == bodyA.getShapeType()) {
						collisionResult = doPolygonsCollide(bodyA, bodyB);
					} else {
						collisionResult = doCirclesCollide(bodyA, bodyB);
					}
				}else {

					if(FlatBody.BOX_SHAPE == bodyA.getShapeType()) {
						collisionResult = doCirclePolygonCollide(bodyB, bodyA);
					}else {
						collisionResult = doCirclePolygonCollide(bodyA, bodyB);
					}
				}

				if(collisionResult.isIntersect) {
					outlineColorList.get(i).set(Color.RED);
					outlineColorList.get(j).set(Color.RED);
					System.out.println(bodyA.getShapeType() == bodyB.getShapeType());
					System.out.println(bodyA.getShapeType());

					bodyB.move(collisionResult.normal.multiply(collisionResult.depth).divide(2f));
					bodyA.move(collisionResult.normal.negative());
				}

			}
		}
	}

	private Collisions.CollisionResult doCirclePolygonCollide(FlatBody circle, FlatBody polygon) {
		return Collisions.detectIntersectCirclePolygon(circle.getPosition(), circle.getRadius(), polygon.getTransformedVertices());
	}

	private Collisions.CollisionResult doCirclesCollide(FlatBody bodyA, FlatBody bodyB) {
		return Collisions.detectIntersectCircles(
				bodyA.getPosition(), bodyA.getRadius(),
				bodyB.getPosition(), bodyB.getRadius());
	}

	private Collisions.CollisionResult doPolygonsCollide(FlatBody bodyA, FlatBody bodyB) {
		FlatVector[] verticesA = bodyA.getTransformedVertices();
		FlatVector[] verticesB = bodyB.getTransformedVertices();
		return Collisions.detectIntersectPolygons(verticesA, verticesB);
	}

	private void drawList() {
		for (int i = 0; i < this.bodyList.size(); i++) {
			drawer.setDefaultLineWidth(strokeWidth);

			FlatBody body = this.bodyList.get(i);

			float x = body.getPosition().getX();
			float y = body.getPosition().getY();

			if(FlatBody.CIRCLE_SHAPE == body.getShapeType()) {
				drawer.setColor(this.outlineColorList.get(i));
				drawer.filledCircle(x, y, body.getRadius());

				drawer.setColor(this.colorList.get(i));
				drawer.filledCircle(x, y, body.getRadius() - 2);
			}else {
				toFloatArray(body.getTransformedVertices());
				drawer.setColor(this.colorList.get(i));
				drawer.filledPolygon(this.cachedVertices, body.getTriangles());

				drawer.setDefaultLineWidth(2f);
				drawer.setColor(this.outlineColorList.get(i));
				drawer.polygon(this.cachedVertices);
			}

		}
	}

	private void toFloatArray(FlatVector[] vertices) {
		this.cachedVertices = new float[vertices.length << 1];

		for (int i = 0, j = 0; i < cachedVertices.length - 1 && j < vertices.length; i+=2, j++) {
			cachedVertices[i] = vertices[j].getX();
			cachedVertices[i + 1] = vertices[j].getY();
		}
	}

	private void drawCircleList() {

		for (int i = 0; i < this.bodyList.size(); i++) {
			FlatBody body = this.bodyList.get(i);

			float x = body.getPosition().getX();
			float y = body.getPosition().getY();

			drawer.setColor(Color.WHITE);
			drawer.filledCircle(x, y, body.getRadius());

			drawer.setColor(this.colorList.get(i));
			drawer.filledCircle(x, y, body.getRadius() - 2);

		}

	}

	private void initializeCircleList() {
		initializeList();

		for (int i = 0; i < 1000; i++) {
			FlatVector center = new FlatVector( (float) Math.random() * totalWidth, (float) Math.random() * totalHeight);
//			System.out.println(center);
			bodyList.add(FlatBody.createCircleBody(20f, center, 2f, false, 0.5f));

			colorList.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
		}
	}

	private void initializeBoxList() {
		initializeList();

		for (int i = 0; i < 100; i++) {
			FlatVector center = new FlatVector( (float) Math.random() * totalWidth, (float) Math.random() * totalHeight);
//			System.out.println(center);
			bodyList.add(FlatBody.createBoxBody(40f, 40f, center, 2f, false, 0.5f));

			colorList.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
			outlineColorList.add(new Color(Color.WHITE));
		}
	}

	private void circleCollide() {
		for (int i = 0; i < this.bodyList.size() - 1; i++) {

			FlatBody bodyA = this.bodyList.get(i);

			for (int j = i + 1; j < this.bodyList.size(); j++) {
				FlatBody bodyB = this.bodyList.get(j);

				Collisions.CollisionResult collisionResult = Collisions.detectIntersectCircles(
						bodyA.getPosition(), bodyA.getRadius(),
						bodyB.getPosition(), bodyB.getRadius());

				if(collisionResult.isIntersect) {
					bodyB.move(collisionResult.normal.multiply(collisionResult.depth).divide(2f));
					bodyA.move(collisionResult.normal.negative());
				}
			}
		}
	}

	private void boxMove(float deltaX, float deltaY, float speed) {
		for (int i = 0; i < this.bodyList.size(); i++) {
			FlatBody body = this.bodyList.get(i);

//			body.rotate((float) Math.PI / 2f / 100f);
			outlineColorList.get(i).set(Color.WHITE);
		}
	}

	private void boxCollide() {
		for (int i = 0; i < this.bodyList.size() - 1; i++) {

			FlatBody bodyA = this.bodyList.get(i);
			FlatVector[] verticesA = bodyA.getTransformedVertices();

			for (int j = i + 1; j < this.bodyList.size(); j++) {
				FlatBody bodyB = this.bodyList.get(j);
				FlatVector[] verticesB = bodyB.getTransformedVertices();

			}
		}
	}

	private void drawBoxList() {
		for (int i = 0; i < this.bodyList.size(); i++) {
			FlatBody body = this.bodyList.get(i);

			toFloatArray(body.getTransformedVertices());
			drawer.setColor(this.colorList.get(i));
			drawer.filledPolygon(this.cachedVertices, body.getTriangles());

			drawer.setDefaultLineWidth(2f);
			drawer.setColor(this.outlineColorList.get(i));
			drawer.polygon(this.cachedVertices);
			drawer.setDefaultLineWidth(strokeWidth);

		}
	}


}
