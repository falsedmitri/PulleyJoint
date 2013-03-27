package com.joint;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;

public class PulleyActivity extends SimpleBaseGameActivity {

	private final int CAMERA_WIDTH = 720; // right edge of screen
	private final int CAMERA_HEIGHT = 480; // bottom of screen

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;

	private Body yellowRectangleBody;
	private Body blueRectangleBody;

	private Vector2 mWorldAxis;
	private Body mGroundBody;

	public EngineOptions onCreateEngineOptions() {

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		return engineOptions;

	}

	@Override
	protected void onCreateResources() {

	}

	@Override
	public Scene onCreateScene() {

		mScene = new Scene();

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		this.mGroundBody = this.mPhysicsWorld.createBody(new BodyDef());

		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1.0f, 0.0f, 1.0f);

		Rectangle blueRectangle = new Rectangle(200, 200, 30, 30, this.getVertexBufferObjectManager());
		blueRectangle.setColor(Color.BLUE);
		blueRectangleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, blueRectangle, BodyType.DynamicBody, fixtureDef);
		blueRectangle.setUserData(blueRectangleBody);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(blueRectangle, blueRectangleBody, true, true));

		Rectangle yellowRectangle = new Rectangle(550, 200, 30, 30, this.getVertexBufferObjectManager());
		yellowRectangle.setColor(Color.YELLOW);
		yellowRectangleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, yellowRectangle, BodyType.DynamicBody, fixtureDef);
		yellowRectangle.setUserData(yellowRectangleBody);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(yellowRectangle, yellowRectangleBody, true, true));

		mWorldAxis = new Vector2(0.0f, 1.0f); // constrain movement to y direction

		createPulleyJoint();

		mScene.attachChild(blueRectangle);
		mScene.attachChild(yellowRectangle);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		return mScene;

	}

	private void createPulleyJoint() {

		Vector2 anchorBlue = blueRectangleBody.getWorldCenter();
		Vector2 anchorYellow = yellowRectangleBody.getWorldCenter();

		Vector2 groundAnchorBlue = new Vector2(anchorBlue.x, anchorBlue.y - (100 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
		Vector2 groundAnchorYellow = new Vector2(anchorYellow.x, anchorYellow.y - (300 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));

		// the greater the ratio, the less the yellow square moves with respect to the blue square
		float ratio = 5.0f;

		PulleyJointDef pulleyJointDef = new PulleyJointDef();

		pulleyJointDef.initialize(blueRectangleBody, yellowRectangleBody, groundAnchorBlue, groundAnchorYellow, anchorBlue, anchorYellow, ratio);

//		pulleyJointDef.maxLengthA = 300 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
//		pulleyJointDef.maxLengthB = 300 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		pulleyJointDef.lengthA = 300 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		pulleyJointDef.lengthB = 300 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

		this.mPhysicsWorld.createJoint(pulleyJointDef);

	}

}
