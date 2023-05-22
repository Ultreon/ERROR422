#version 110

uniform sampler2D LastFrame;
uniform sampler2D DiffuseSampler;

uniform float fright;
uniform float fear_zoom;

uniform float iTime;

varying vec2 texCoord;

float rand(vec2 p)
{
    float t = floor(iTime * 20.) / 10.;
    return fract(sin(dot(p, vec2(t * 12.9898, t * 78.233))) * 43758.5453);
}

float noise(vec2 uv, float blockiness)
{
    vec2 lv = fract(uv);
    vec2 id = floor(uv);

    float n1 = rand(id);
    float n2 = rand(id+vec2(1,0));
    float n3 = rand(id+vec2(0,1));
    float n4 = rand(id+vec2(1,1));

    vec2 u = smoothstep(0.0, 1.0 + blockiness, lv);

    return mix(mix(n1, n2, u.x), mix(n3, n4, u.x), u.y);
}

float fbm(vec2 uv, int count, float blockiness, float complexity)
{
    float val = 0.0;
    float amp = 0.5;

    while(count != 0)
    {
        val += amp * noise(uv, blockiness);
        amp *= 0.5;
        uv *= complexity;
        count--;
    }

    return val;
}

const float glitchAmplitude = 3.0; // increase this
const float glitchNarrowness = 4.0;
const float glitchBlockiness = 2.0;
const float glitchMinimizer = 5.0; // decrease this

void main() {
    vec4 zoomTex = texture2D(LastFrame, (texCoord - 0.5) * 0.99 + 0.5);
    vec4 lastTex = texture2D(LastFrame, texCoord);
    vec4 currentTex = texture2D(DiffuseSampler, texCoord);

    vec2 uv = texCoord;
    vec2 uv2 = texCoord;

    float shift = fear_zoom * glitchAmplitude * pow(fbm(uv2, 4, glitchBlockiness, glitchNarrowness), glitchMinimizer);

    float colR = texture2D(DiffuseSampler, vec2(uv.x + shift, uv.y)).r * (1. - shift);
    float colG = texture2D(DiffuseSampler, vec2(uv.x - shift, uv.y)).g * (1. - shift);
    float colB = texture2D(DiffuseSampler, vec2(uv.x - shift, uv.y)).b * (1. - shift);

    gl_FragColor = mix(mix(lastTex, vec4(colR, colG, colB, 1.0), 0.75 * fear_zoom), currentTex, mix(1.0, 0.5, fright));
}