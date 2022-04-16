#version 120

// https://www.shadertoy.com/view/ldfSDj

uniform vec2 size;
uniform vec4 color;
uniform float radius;

float udRoundBox(vec2 p, vec2 b, float r)
{
    return length(max(abs(p) - b + r, 0.0)) - r;
}

void main()
{

    vec2 halfSize = size * 0.5;

    float b = udRoundBox((gl_TexCoord[0].xy * size) - halfSize, halfSize - 1.0, radius);
    vec4 c = mix(color, vec4(color.rgb, 0.0), smoothstep(0.0, 1.0, b));

    gl_FragColor = c;
}