
//With a function : exponential bands : more intuitive way
(
var inputSoundBuffer;
var vocoderIntuitive, running, w, knob, onButton, pause = true;
inputSoundBuffer = Buffer.read(s,"C:/Users/marti/Documents/Ingenieur/Master2/Sound/luck_speech.wav");

vocoderIntuitive = {
	//Carrier must be a function otherwise does not work!
	arg  car = {Saw.ar(220)}, numBands = 20, outputMul = 25, minFreq = 100, maxFreq = 10000;
	{var vocoderStep = {
		arg bandwidth = 1000, freq = 440, carrier = Saw.ar(220);
		var modulator = PlayBuf.ar(2, inputSoundBuffer.bufnum, BufRateScale.kr(inputSoundBuffer.bufnum));
		var filtMod = BPF.ar(modulator, freq, bandwidth/freq);
		var filtAmpl = Amplitude.kr(filtMod);
		var filtCarrier = BPF.ar(carrier, freq, bandwidth/freq);
		filtCarrier * filtAmpl;
	};


	var step = (maxFreq/minFreq)**numBands.reciprocal;
	var curFreq = minFreq;
	var bands = Array.newClear(numBands);

	for(1, numBands,
		{arg i;
			bands.put(i-1, vocoderStep.value((curFreq*step - curFreq), (curFreq + curFreq*step)/2, car));
			curFreq = curFreq * step;
	});

	outputMul * Mix.new(bands)};
};

w= Window("My tunable Vocoder",Rect(100,300,300,200));
knob= Knob(w);
knob.action={if(pause == false,{ free(running); pause = true;});};

onButton = Button(w);
onButton.action = {pause = false;
	running = vocoderIntuitive.value({Saw.ar(knob.value*5000+10)}, 100, 100).play;
};
onButton.string = "Play!";

w.layout = HLayout(onButton, knob);
w.front;

)

var x;
x = true;
