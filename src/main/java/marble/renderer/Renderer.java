package marble.renderer;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL44.glClearTexImage;

import imgui.ImGui;
import marble.editor.ConsolePanel;
import marble.editor.EditorLayer;
import marble.util.Time;
import marble.entity.Material;
import marble.entity.components.Mesh;
import marble.entity.components.Registry;
import marble.entity.components.light.Light;
import marble.entity.components.camera.Camera;
import org.joml.Vector3f;

import java.util.List;

public class Renderer {

    public enum ViewportId { EDITOR, GAME };

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glClearTexImage(EditorLayer.EDITOR_FRAMEBUFFER.redIntTextureId, 0, GL_RED_INTEGER, GL_INT, new int[]{ -1 });
    }

    public void render(Camera camera, Registry registry, Framebuffer frameBuffer, Vector3f ambientLight, float specularPower, ViewportId viewportId)
    {
        frameBuffer.bind();
        clear();

        for (Mesh mesh : registry.getMeshes())
        {
            Material material = mesh.material;
            Shader shader = material.getShader();
            shader.bind();

            List<Light> lights = registry.getLights();
            for (int i = 0; i < registry.getLights().size(); i++)
                shader.setUniformDirLight(lights.get(i), camera.getViewMatrix(), i);

            shader.setUniformMaterial(material);
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniform1i("uEntityID", mesh.getEntity().getUuid());
            shader.setUniform3f("uAmbientLight", ambientLight);
            shader.setUniform1f("uSpecularPower", specularPower);
            shader.setUniformMat4("uView", camera.getViewMatrix());

            if (viewportId == ViewportId.GAME)
                shader.setUniformMat4("uProjection", camera.getProjectionMatrixGame());
            else
                shader.setUniformMat4("uProjection", camera.getProjectionMatrixEditor());

            shader.setUniformMat4("uWorld", camera.getWorldMatrix(mesh.getEntity().transform.position, mesh.getEntity().transform.rotation, mesh.getEntity().transform.scale));

            mesh.render();
            shader.unbind();

        }
        frameBuffer.unbind();
    }

}
