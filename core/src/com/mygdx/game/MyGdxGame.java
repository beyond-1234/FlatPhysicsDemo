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

	ArrayList<FlatBody> bodyList;
	ArrayList<Color> colorList;

	// cache stuff instead of creating new object everytime
	FlatVector cachedDirection;
	float[] cachedVertices;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);

		initializeDrawer();

		initializeCircleList();

		initializeBoxList();
	}


	@Override
	public void render () {
		ScreenUtils.clear(0, 0.3f, 0.3f, 1);
		batch.begin();
		camera.update();

		float deltaX = 0f;
		float deltaY = 0f;
		float speed  = 8f;

//		circleMove(deltaX, deltaY, speed);
//
//		circleCollide();
//
//		drawCircleList();

		boxMove(deltaX, deltaY, speed);

		boxCollide();

		drawBoxList();

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

	private void initializeCircleList() {
		cachedDirection = new FlatVector(0f, 0f);
		colorList = new ArrayList<>();
		bodyList = new ArrayList<>();

		float totalHeight = camera.viewportHeight;
		float totalWidth = camera.viewportWidth;

		for (int i = 0; i < 1000; i++) {
			FlatVector center = new FlatVector( (float) Math.random() * totalWidth, (float) Math.random() * totalHeight);
//			System.out.println(center);
			bodyList.add(FlatBody.createCircleBody(20f, center, 2f, false, 0.5f));

			colorList.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
		}
	}

	private void initializeBoxList() {
		cachedDirection = new FlatVector(0f, 0f);
		colorList = new ArrayList<>();
		bodyList = new ArrayList<>();

		float totalHeight = camera.viewportHeight;
		float totalWidth = camera.viewportWidth;

		for (int i = 0; i < 10; i++) {
			FlatVector center = new FlatVector( (float) Math.random() * totalWidth, (float) Math.random() * totalHeight);
//			System.out.println(center);
			bodyList.add(FlatBody.createBoxBody(40f, 40f, center, 2f, false, 0.5f));

			colorList.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
		}
	}

	private void circleMove(float deltaX, float deltaY, float speed) {
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

				float depth = Collisions.getIntersectCirclesDepth(
							bodyA.getPosition(), bodyA.getRadius(),
							bodyB.getPosition(), bodyB.getRadius());
				if(depth > 0f) {
					FlatVector directionForA = Collisions.getIntersectCirclesNormal(bodyA.getPosition(), bodyB.getPosition());
					FlatVector directionForB = directionForA.clone();
					bodyA.move(directionForA.multiply(depth).negative().divide(2f));
					bodyB.move(directionForB.multiply(depth).divide(2f));
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

			body.rotate((float) Math.PI / 2f / 100f);
		}
	}

	private void boxCollide() {}

	private void drawBoxList() {
		for (int i = 0; i < this.bodyList.size(); i++) {
			FlatBody body = this.bodyList.get(i);

			toFloatArray(body.getTransformedVertices());
			drawer.setColor(this.colorList.get(i));
			drawer.filledPolygon(this.cachedVertices, body.getTriangles());

			drawer.setDefaultLineWidth(2f);
			drawer.setColor(Color.WHITE);
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
