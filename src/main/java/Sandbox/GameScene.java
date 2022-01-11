package Sandbox;

import Marble.Camera.Camera;
import Marble.GameObject.Components.Mesh;
import Marble.GameObject.GameObject;
import Marble.GameObject.Transform;
import Marble.Listeners.KeyListener;
import Marble.Scene.Scene;
import Marble.Window;
import org.joml.Vector3f;

import java.awt.event.KeyEvent;

public class GameScene extends Scene {

    public GameScene()
    {
        System.out.println("Inside game scene.");
    }

    @Override
    public void init()
    {
        camera = new Camera(new Vector3f(0,0,10));

        float[] positions = new float[] {
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                 0.5f, -0.5f,  0.5f,
                 0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                 0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                 0.5f, -0.5f, -0.5f,
        };
        int[] indices = new int[] {
                0, 1, 3, 3, 1, 2,
                4, 0, 3, 5, 4, 3,
                3, 2, 7, 5, 3, 7,
                6, 1, 0, 6, 0, 4,
                2, 1, 6, 2, 6, 7,
                7, 6, 4, 7, 4, 5,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        {
            GameObject go = new GameObject("Cube1", new Transform(new Vector3f(1.5f,1.2f,1), new Vector3f(30, 78, 10), 1));
            go.addComponent(new Mesh(positions, colors, indices));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Cube2", new Transform(new Vector3f(1,2,4), new Vector3f(66, 5, 17), 1));
            go.addComponent(new Mesh(positions, colors, indices));
            addGameObjectToScene(go);
        }
    }

    @Override
    public void update(float dt)
    {
        float camSpeed = 40f;
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.move(0, camSpeed,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.move(0, -camSpeed,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.move(-camSpeed,0,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.move(camSpeed,0,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            camera.move(0,0, -camSpeed, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            camera.move(0,0, camSpeed, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            Window.changeScene(0);

        for (GameObject gameObject : gameObjects)
            gameObject.update(dt);

        for (GameObject gameObject : gameObjects) {
            float rotation = gameObject.transform.rotation.z + 0.5f;
            if (rotation > 360) {
                rotation = 0;
            }
            gameObject.transform.setRotation(rotation, rotation, rotation);
        }

        renderer.render(camera);
    }
}
