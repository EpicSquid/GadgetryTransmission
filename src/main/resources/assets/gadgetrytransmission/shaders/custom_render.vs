#version 120
varying vec3 normal;
varying vec3 position;
varying vec4 uv;
varying vec3 lightsum;

uniform sampler2D sampler;
uniform sampler2D lightmap;

float rand2(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 rand3(vec3 co){
    return vec3(rand2(co.xz)-0.5f,rand2(co.yx)-0.5f,rand2(co.zy)-0.5f);
}

float distSq(vec3 a, vec3 b){
	return pow((a.x-b.x),2)+pow((a.y-b.y),2)+pow((a.z-b.z),2);
}

void main()
{
    vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	normal = gl_Normal;
	
	position = gl_Vertex.xyz;
	
	float offset = 0;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_MultiTexCoord1;
	
	gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex + vec4(0,offset,0,0));
	
	vec4 ambtot = vec4(0);
	vec4 difftot = vec4(0);
	
	for (int i = 0; i < 9; i ++){
		vec4 amb = gl_FrontLightProduct[i].ambient;    
		
		vec4 diff = gl_FrontLightProduct[i].diffuse * max(dot(normal,gl_LightSource[i].position.xyz), 0.0f);
		diff = clamp(diff, 0.0f, 1.0f);     
		
		difftot += diff;
	}
	
	ambtot += gl_LightModel.ambient;
	
	vec3 templight = (ambtot + difftot).xyz;
	lightsum = clamp(templight, 0.0f, 1.0f);

	gl_FrontColor = gl_Color;
}