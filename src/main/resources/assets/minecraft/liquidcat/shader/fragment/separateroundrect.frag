#version 120

uniform vec2 size;
uniform vec4 color;
uniform vec4 radius;

float sdRoundBox(in vec2 p, in vec2 b, in vec4 r)
{
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;

    vec2 q = abs(p) - b + r.x;

    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main()
{
    vec2 halfSize = size * 0.5;

    float b = sdRoundBox((gl_TexCoord[0].xy * size) - halfSize, halfSize - 1.0, radius);
    vec4 c = mix(color, vec4(color.rgb, 0.0), smoothstep(0.0, 1.0, b));

    gl_FragColor = c;
}