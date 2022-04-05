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
	Texture img;

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
		camera.setToOrtho(false, 1920, 1080);
		totalHeight = camera.viewportHeight;
		totalWidth  = camera.viewportWidth;

		initializeDrawer();

		initializeCircleList();

//		initializeBoxList();
	}


	@Override
	public void render () {
		ScreenUtils.clear(0, 0.3f, 0.3f, 1);
		batch.begin();
		camera.update();

		float deltaX = 0f;
		float deltaY = 0f;
		float speed  = 8f;

		move(deltaX, deltaY, speed);

		circleCollide();

		drawCircleList();

//		boxMove(deltaX, deltaY, speed);
//
//		boxCollide();
//
//		drawBoxList();

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
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

				Collisions.CollisionResult collisionResult = Collisions.detectIntersectPolygons(verticesA, verticesB);
				if(collisionResult.isIntersect) {
					outlineColorList.get(i).set(Color.RED);
					outlineColorList.get(j).set(Color.RED);

//					FlatVector directionForB = collisionResult.normal.clone();
					bodyB.move(collisionResult.normal.multiply(collisionResult.depth).divide(2f));
					bodyA.move(collisionResult.normal.negative());
				}

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

	private void toFloatArray(FlatVector[] vertices) {
		this.cachedVertices = new float[vertices.length << 1];

		for (int i = 0, j = 0; i < cachedVertices.length - 1 && j < vertices.length; i+=2, j++) {
			cachedVertices[i] = vertices[j].getX();
			cachedVertices[i + 1] = vertices[j].getY();
		}
	}

}
