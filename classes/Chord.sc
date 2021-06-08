// requires quark miSCellaneous_lib 0.24.0
Chord {

	var <>tuning;
	var <>fractions;
	var <>notes;
	var <>freqs;
	var <>freqs_norm_fraction;

	*initClass {

		// var tuning;
		// tuning = Tuning.just;
		// (1..8).do({ |i|
		// 	if (i==1,
		// 		{
		// 			"init".debug;
		// 			fractions = tuning.ratios.asFraction;
		// 		},
		// 		{
		// 			i.debug;
		// 			fractions = fractions ++ tuning.ratios.asFraction.collect({|f| [f[0]*i,f[1]];});
		// 	});
		// });
	}

	*new { |notes|
		^super.new.notes = notes;
	}

	asNotes {
		^this.notes;
	}

	asFraction { |max = 10|
		var semitones, freqs_norm;

		this.tuning = Tuning.just;
		(1..8).do({ |i|
			if (i==1,
				{
					//"init".debug;
					this.fractions = tuning.ratios.asFraction;
				},
				{
					//i.debug;
					this.fractions = this.fractions ++ tuning.ratios.asFraction.collect({|f| [f[0]*(2.pow(i-1).asInteger),f[1]];});
			});
		});

//		semitones = this.notes.collect({|n| n-this.notes[0];});
//		this.tuning.
		this.freqs = this.notes.midicps;
		//freqs_norm = (this.freqs/this.freqs[0]);
		//freqs_norm.postln;
		//this.freqs_norm_fraction = freqs_norm.asFraction(max); // normalize by base note
		this.freqs_norm_fraction = fractions.obtain(this.notes-this.notes[0]);
		^this.freqs_norm_fraction;
	}

	// maximum error in cents with given precision of approximating fraction to freq in float
	maxError { |precision=0.01|
		var freqs_norm_ratio, error;

		// error
		freqs_norm_ratio = this.freqs_norm_fraction.collect({|f| (f[0].asFloat / f[1])*this.freqs[0] });
		error = (this.freqs.cents-freqs_norm_ratio.cents).round(precision);

		("error: ").debug;
		error.do({|e, i|
			(" (" + freqs_norm_ratio[i][0] + "/" +freqs_norm_ratio[i][1] + ") " + e).debug;
		});

		^error.reduce(\max);
	}


	// harmonic dissonance (Cubarsi, 2019: 'Harmonic distance in intervals and chords')
	harmonicDissonance { |max = 50, precision = 0.01|
		var freqs, freqs_norm_fraction, freqs_norm_ratio, error, p, q, p_lcm, p_gcd, q_lcm, q_gcd, d;

		if (notes.size == 0,
			{^0;},
			{

				freqs_norm_fraction = this.asFraction(max);
				//freqs_norm_fraction.debug;
				freqs = this.freqs;

				// error
				freqs_norm_ratio = freqs_norm_fraction.collect({|f| (f[0].asFloat / f[1])*freqs[0]; });
				error = (freqs.collect({|f| f.cents;}) - freqs_norm_ratio.collect({|f| f.cents;})).round(precision);

				("freq_norm_fraction:  ").debug;
				freqs_norm_fraction.do({|f, i|
					(" " + i + ": (" + f[0] + "/" + f[1] + ") " + error[i] + " | " + freqs_norm_ratio[i] + " - " + freqs[i]).debug;
				});

				// reshape the array
				p = freqs_norm_fraction.collect({|f| f[0]}); // numerators
				q = freqs_norm_fraction.collect({|f| f[1]}); // denominators

				// Note: lcm silently overflows the 32-bit integer range, so instead use factors
				p_lcm = p.lcmByFactors; // .reduce(\lcm).postln;
				if (p_lcm.isNumber, {p_lcm = [p_lcm];}, {p_lcm = p_lcm[1];});
				p_gcd = p.reduce(\gcd);
				q_lcm = q.lcmByFactors; // reduce(\lcm).postln;
				if (q_lcm.isNumber, {q_lcm = [q_lcm];}, {q_lcm = q_lcm[1];});
				q_gcd = q.reduce(\gcd);


				d = (p_lcm/p_gcd).log2.reduce('+') + (q_lcm/q_gcd).log2.reduce('+'); // distance according to Cubarsi, 2019

				("lcmByFactors:").debug;
				(" (" + p_lcm + "/" + q_lcm + ") ").debug;

				("harmonicDissonance: " + d).debug;
				^d;
		});
	}

	*harmonicDissonance { |notes, max = 50|
		var c;
		c = super.new.notes = notes;
		//c.asFraction.postln;
		^c.harmonicDissonance(max);
	}

	printOn { | stream |
		if (this.notes.size > 0, {
			stream << "Chord( " << this.notes << ", " << this.harmonicDissonance << " )";
		},
		{});
	}

}