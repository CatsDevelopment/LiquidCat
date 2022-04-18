#version 120

uniform sampler2D texture;
uniform vec2 texelSize;
uniform vec3 color;
uniform float radius, divider;

void main() {
    vec4 c = texture2D(texture, gl_TexCoord[0].xy);

    if (c.a == 0) {
        float alpha = 0.0;

        for (float x = -radius; x < radius; x++) {
            for (float y = -radius; y < radius; y++) {
                vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + texelSize.xy * vec2(x, y));

                if (currentColor.a != 0) {
                    alpha += max(0.0, radius - distance(vec2(x, y), vec2(0)));
                }
            }
        }

        gl_FragColor = vec4(color, alpha / divider);
    } else {
        gl_FragColor = vec4(0.0);
    }
}