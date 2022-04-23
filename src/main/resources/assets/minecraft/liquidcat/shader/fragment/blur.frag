#version 120

uniform sampler2D texture;
uniform vec2 direction, texelsize;
uniform int radius;
uniform float weights[128];

// Gaussian blur
// https://www.rastergrid.com/blog/2010/09/efficient-gaussian-blur-with-linear-sampling

void main()
{
    vec3 blurred = texture2D(texture, gl_TexCoord[0].st).rgb * weights[0];

    for (int i = 1; i <= radius; i++)
    {
        vec2 c = i * (texelsize * direction);

        blurred += texture2D(texture, gl_TexCoord[0].st + c).rgb * weights[i];
        blurred += texture2D(texture, gl_TexCoord[0].st - c).rgb * weights[i];
    }

    gl_FragColor = vec4(blurred, 1.0);
}