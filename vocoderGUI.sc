
//With a function : exponential bands : more intuitive way
(
var inputSoundBuffer;
var vocoderIntuitive, vocoderGUI;
inputSoundBuffer = Buffer.read(s,"C:/Users/marti/Documents/Ingenieur/Master2/Sound/Vocoder/luck_speech.wav");

//Vocoder code
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

//GUI definition
vocoderGUI = {
	var running, w, pause = true;
	var freqKnob, freqKnobLabel, freqKnobNr, freqMult = 2000, freqShift = 10;
	var bandsKnob, bandsKnobLabel, bandsKnobNr, bandsMult = 100, bandsShift = 1;
	var volKnob, volKnobLabel, volKnobNr, volMult = 100, freqShift = 1;
	var onButton;
	w= Window("My tunable Vocoder",Rect(100,300,300,200));

	//Knob to tune frequency
	freqKnob= Knob(w);
	freqKnobNr = NumberBox(w);
	freqKnob.action={if(pause == false,{ free(running); pause = true;});
		freqKnobNr.value = freqKnob.value*freqMult+freqShift;
	};

	freqKnobLabel = StaticText(w);
	freqKnobLabel.string = "Frequency";
	freqKnobLabel.align = \center;

	freqKnobNr.value = freqKnob.value*freqMult+freqShift;
	freqKnobNr.action = {if(pause == false,{ free(running); pause = true;});
		freqKnob.value = (freqKnobNr.value-freqShift)/freqMult;
	};

	//Knob to tune number of bands
	bandsKnob= Knob(w);
	bandsKnobNr = NumberBox(w);
	bandsKnob.action={if(pause == false,{ free(running); pause = true;});
		bandsKnobNr.value = round(bandsKnob.value*bandsMult+bandsShift);
	};
	bandsKnob.value = 0.5;

	bandsKnobLabel = StaticText(w);
	bandsKnobLabel.string = "Bands";
	bandsKnobLabel.align = \center;

	bandsKnobNr.value = round(bandsKnob.value*bandsMult+bandsShift);
	bandsKnobNr.action = {if(pause == false,{ free(running); pause = true;});
		bandsKnob.value = (bandsKnobNr.value-bandsShift)/bandsMult;
	};

	//Knob to tune volume
	volKnob= Knob(w);
	volKnobNr = NumberBox(w);
	volKnob.action={if(pause == false,{ free(running); pause = true;});
		volKnobNr.value = volKnob.value*volMult+volShift;
	};
	volKnob.value = 0.25;

	volKnobLabel = StaticText(w);
	volKnobLabel.string = "Volume";
	volKnobLabel.align = \center;

	volKnobNr.value = volKnob.value*volMult+volShift;
	volKnobNr.action = {if(pause == false,{ free(running); pause = true;});
		volKnob.value = (volKnobNr.value-volShift)/volMult;
	};

	//Button to start the sound
	onButton = Button(w);
	onButton.action = {pause = false;
		running = vocoderIntuitive.value({Saw.ar(freqKnob.value*freqMult+freqShift)}, round(bandsKnob.value*bandsMult + bandsShift), volKnob.value*volMult+volShift).play;
	};
	onButton.string = "Play!";

	//Define an automatic layout
	w.layout = HLayout(onButton, VLayout(freqKnobLabel, freqKnob, freqKnobNr), VLayout(bandsKnobLabel, bandsKnob, bandsKnobNr), VLayout(volKnobLabel, volKnob, volKnobNr));
	w.front;
};

vocoderGUI.value;
)