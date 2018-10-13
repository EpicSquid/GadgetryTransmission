#version 120

varying vec3 normal;
varying vec3 position;
varying vec4 uv;
varying vec3 lightsum;

uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform vec3 playerPos;

void main()
{
	vec3 lightdark = texture2D(lightmap,gl_TexCoord[1].st).xyz;
	lightdark = clamp(lightdark,0.0f,1.0f);
	
	vec4 baseColor = gl_Color * texture2D(sampler,gl_TexCoord[0].st);
	
	vec3 dv = position-playerPos;
	float dist = max(sqrt(dv.x*dv.x+dv.y*dv.y+dv.z*dv.z) - gl_Fog.start,0.0f) / (gl_Fog.end-gl_Fog.start);
	
	float fog = gl_Fog.density * dist;
				  
	fog = 1.0f-clamp( fog, 0.0f, 1.0f );
	  
	baseColor = vec4(mix( vec3( gl_Fog.color ), baseColor.xyz, fog ).xyz,baseColor.w);
	
	vec4 color = vec4(baseColor.xyz * lightdark * lightsum,baseColor.w);
	
	gl_FragColor = color;
}