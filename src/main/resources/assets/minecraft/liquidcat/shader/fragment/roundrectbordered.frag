#version 120

uniform vec4 rectColor, borderColor;
uniform vec2 size;
uniform float radius, borderThickness;

// https://www.shadertoy.com/view/ldfSDj
float udRoundBox(vec2 p, vec2 b, float r)
{
    return length(max(abs(p) - b + r, 0.0)) - r;
}

void main()
{
    vec2 halfSize = size * 0.5;

    float b = udRoundBox((gl_TexCoord[0].xy * size) - halfSize, halfSize - 1.0, radius);
    float alpha = 1.0f - smoothstep(0.0f, 1.0, b);

    float borderAlpha = 1.0 - smoothstep(borderThickness - 1.0, borderThickness, abs(b));

    gl_FragColor = mix(vec4(borderColor.rgb, 0.0), mix(rectColor, borderColor, borderAlpha), alpha);
}