// requires quark miSCellaneous_lib 0.24.0
Chord {

	var <>notes;
	var <>freqs;
	var <>freqs_norm_fraction;

	*new { |notes| ^super.new.notes = notes; }

	asNotes {
		^this.notes;
	}

	asFraction { |max = 10|
		var freqs_norm;
		this.freqs = this.notes.midicps;
		freqs_norm = (this.freqs/this.freqs[0]);
		//freqs_norm.postln;
		this.freqs_norm_fraction = freqs_norm.asFraction(max); // normalize by base note
		^this.freqs_norm_fraction;
	}

	// maximum error in cents with given precision of approximating fraction to freq in float
	maxError { |precision=0.01|
		var freqs_norm_ratio, error;

		// error
		freqs_norm_ratio = this.freqs_norm_fraction.collect({|f| (f[0].asFloat / f[1])*this.freqs[0] });
		error = (this.freqs.cents-freqs_norm_ratio.cents).round(precision);
		^error.reduce(\max);
	}


	// harmonic dissonance (Cubarsi, 2019: 'Harmonic distance in intervals and chords')
	harmonicDissonance { |max = 50|
		var freqs, freqs_norm_fraction, freqs_norm_ratio, error, p, q, p_lcm, p_gcd, q_lcm, q_gcd, d;

		if (notes.size == 0,
			{^0;},
			{

		freqs_norm_fraction = this.asFraction(max);
		freqs = this.freqs;

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

		^d = (p_lcm/p_gcd).log2.reduce('+') + (q_lcm/q_gcd).log2.reduce('+'); // distance according to Cubarsi, 2019
		});
	}

	*harmonicDissonance { |notes|
		var c;
		c = super.new.notes = notes;
		//c.asFraction.postln;
		^c.harmonicDissonance();
	}

	printOn { | stream |
		if (this.notes.size > 0, {
		stream << "Chord( " << this.notes << ", " << this.harmonicDissonance << " )";
		},
		{});
    }

}