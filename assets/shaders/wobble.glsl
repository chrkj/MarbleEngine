#type vertex
#version 460 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;
layout (location=2) in vec3 aVertexNormal;

uniform mat4 uView;
uniform mat4 uWorld;
uniform mat4 uProjection;
uniform float uTime;

out vec2 fTexCoord;
out vec3 fVertexPos;
out vec3 fVertexNormal;

void main()
{
    vec3 transformed;
    transformed.x = aPos.x + sin(aPos.y * 3.5 + uTime * 3.5) * 0.1;
    transformed.y = aPos.y + sin(aPos.z * 3.5 + uTime * 3.5) * 0.1;
    transformed.z = aPos.z + sin(aPos.x * 3.5 + uTime * 3.5) * 0.1;

    gl_Position = uProjection * uView * uWorld * vec4(transformed, 1.0);
    fTexCoord = aTexCoord;
    fVertexNormal = normalize(uView * uWorld * vec4(aVertexNormal, 0.0)).xyz;
    fVertexPos = (uView * uWorld * vec4(aPos, 1.0)).xyz;
}


    #type fragment
    #version 460 core
in vec2 fTexCoord;
in vec3 fVertexPos;
in vec3 fVertexNormal;

out vec4 fragColor;

struct DirectionalLight
{
    vec4 color;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

uniform Material uMaterial;
uniform DirectionalLight uDirectionalLight;
uniform vec3 uAmbientLight;
uniform float uSpecularPower;
uniform sampler2D uTextureSampler;

void setupColors(Material uMaterial, vec2 textCoord)
{
    if (uMaterial.hasTexture == 1)
    {
        ambientC = texture(uTextureSampler, textCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
    }
    else
    {
        ambientC = uMaterial.ambient;
        diffuseC = uMaterial.diffuse;
        speculrC = uMaterial.specular;
    }
}

vec4 calcLightColor(vec4 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * light_color * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, uSpecularPower);
    specColor = speculrC * light_intensity * specularFactor * uMaterial.reflectance * light_color;

    return (diffuseColor + specColor);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}


void main()
{
    setupColors(uMaterial, fTexCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(uDirectionalLight, fVertexPos, fVertexNormal);;

    fragColor = ambientC * vec4(uAmbientLight, 1) + diffuseSpecularComp;
}