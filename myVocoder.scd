b = Buffer.read(s,"C:/Users/marti/Documents/Ingenieur/Master2/Sound/Vocoder/Modulators/luck_speech.wav");
Platform.resourceDir




SynthDef(\vocoderStep, {
	arg bandwidth = 1000, freq = 440;
	var carrier = Saw.ar(freq);
	var modulator = PlayBuf.ar(2, b.bufnum, BufRateScale.kr(b.bufnum));
	var filtMod = BPF.ar(modulator, freq, bandwidth/freq);
	var filtAmpl = Amplitude.kr(filtMod);
	var filtCarrier = BPF.ar(carrier, freq, bandwidth/freq);
	Out.ar(0,filtCarrier * filtAmpl);
}
).add;

Synth(\vocoderStep, [\bandwith, 1000, \freq, 440]);


//With a function : linear steps
(
{var vocoderStep = {
	arg bandwidth = 1000, freq = 440;
	var carrier = Saw.ar(freq);
	var modulator = PlayBuf.ar(2, b.bufnum, BufRateScale.kr(b.bufnum));
	var filtMod = BPF.ar(modulator, freq, bandwidth/freq);
	var filtAmpl = Amplitude.kr(filtMod);
	var filtCarrier = BPF.ar(carrier, freq, bandwidth/freq);
	25 * filtCarrier * filtAmpl;
};

var numBands = 20;
var minFreq = 100;
var maxFreq = 10000;
var step = (maxFreq - minFreq) / numBands;
var curFreq = minFreq;
var bands = Array.newClear(numBands);

for(1, numBands,
	{arg i;
		bands.put(i-1, vocoderStep.value(step, curFreq));
		curFreq = curFreq + step;
});

bands.postln;
Mix.new(bands)}.scope;
)

//With a function : exponential bands, same way as Vocoder.sc
(
{var vocoderStep = {
	arg bandwidth = 1000, freq = 440;
	var carrier = Saw.ar(freq);
	var modulator = PlayBuf.ar(2, b.bufnum, BufRateScale.kr(b.bufnum));
	var filtMod = BPF.ar(modulator, freq, bandwidth/freq);
	var filtAmpl = Amplitude.kr(filtMod);
	var filtCarrier = BPF.ar(carrier, freq, bandwidth/freq);
	25 * filtCarrier * filtAmpl;
};

var numBands = 20;
var minFreq = 100;
var maxFreq = 10000;
var step = /*(maxFreq - minFreq) / numBands;*/(maxFreq/minFreq)**numBands.reciprocal;
var curFreq = minFreq;
var bands = Array.newClear(numBands);

for(1, numBands,
	{arg i;
		bands.put(i-1, vocoderStep.value(0.02*curFreq, curFreq));
		curFreq = curFreq * step;
});

bands.postln;
Mix.new(bands)}.scope;
)

//With a function : exponential bands : more intuitive way
(
var vocoderIntuitive;
vocoderIntuitive = {
	//Carrier must be a function otherwise does not work!
	arg  car = {Saw.ar(220)}, numBands = 20, outputMul = 25, minFreq = 100, maxFreq = 10000;
	{var vocoderStep = {
		arg bandwidth = 1000, freq = 440, carrier = Saw.ar(220);
		var modulator = PlayBuf.ar(2, b.bufnum, BufRateScale.kr(b.bufnum));
		var filtMod = BPF.ar(modulator, freq, bandwidth/freq);
		var filtAmpl = Amplitude.kr(filtMod);
		var filtCarrier = BPF.ar(carrier, freq, bandwidth/freq);
		carrier.postln;
		filtCarrier * filtAmpl;
	};


	var step = (maxFreq/minFreq)**numBands.reciprocal;
	var curFreq = minFreq;
	var bands = Array.newClear(numBands);

	for(1, numBands,
		{arg i;
			bands.put(i-1, vocoderStep.value((curFreq*step - curFreq)*0.8, (curFreq + curFreq*step)/2, car));
			curFreq = curFreq * step;
	});

	bands.postln;
	outputMul * Mix.new(bands)};
};

vocoderIntuitive.value({Saw.ar(200)}, 50, 100).scope;
)

{Vocoder.ar(Saw.ar(200,4),PlayBuf.ar(2, b.bufnum, BufRateScale.kr(b.bufnum)),50, 100, 5000, 0.02, 5000, 0.05, 100)}.play;
