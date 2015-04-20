{\rtf1\ansi\ansicpg1252\cocoartf1344\cocoasubrtf720
\cocoascreenfonts1{\fonttbl\f0\fnil\fcharset0 Monaco;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;\red0\green0\blue191;\red191\green0\blue0;
\red0\green0\blue255;\red102\green102\blue191;\red0\green115\blue0;\red96\green96\blue96;\red51\green51\blue191;
}
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardirnatural

\f0\fs18 \cf2 + \cf3 SimpleNumber\cf2  \{\
\cf4 //	 The following methods are also defined for SequenceableCollection below.\cf2 \
\cf4 //	 Methods that can be applied to rational numbers will work with arrays of [p,q]'s\cf2 \
\
	\cf4 //ex. 3.cents(2) or 440.cents(60.midicps) or [2,3].cents\cf2 \
\cf4 //	cents \{ | frq = 1| \cf2 \
\cf4 //		^1200 * ( (this/frq).log / 2.log )\cf2 \
\cf4 //	\}\cf2 \
\cf4 // used to be the mathematical formula above but was changed for efficiency to:\cf2 \
	cents \{\cf5 |frq=1|\cf2 \
		^( (\cf6 this\cf2 /frq).ratiomidi * 100 )\
	\}\
	\
	\cf4 //ex. 440.addCents(Array.series(12, 0, 100)).asNote\cf2 \
	addCents \{ \cf5 |cents|\cf2 \
		^\cf6 this\cf2  * (2**(cents/1200))\
	\}\
	\
	\cf4 // basically the same but with different semantics: \cf2 \
	\cf4 // ex. 833.cents2Frq\cf2 \
	cents2Frq \{\cf5 |frq = 1|\cf2  ^frq.addCents(\cf6 this\cf2 )\}\
\
	asNote \{ \cf5 var\cf2  residue, octave, note, roundedNote;\
		note = \cf6 this\cf2 .cpsmidi; \
		roundedNote = note.round(1);\
		octave = ((roundedNote / 12).asInteger) - 1;\
		residue  = (note.frac * 100).round(1);\
		if (residue >= 50) \{\
			^[\cf3 NoteNames\cf2 .flatnames[(roundedNote - 72) % 12].asString ++ octave.asString, \
				(100 - residue).neg]\
		\}\{\
			^[\cf3 NoteNames\cf2 .names[(roundedNote - 72) % 12].asString ++ octave.asString, \
				residue];\
		\}\
	\}\
	\
	\cf4 // utility to calculate pitch bends for midi playback of microtones\cf2 \
	\cf4 // this is in cents, pb is the pitch bend ammount (400 = +- 1 tone, i.e. -200 to +200 cents)\cf2 \
	\cf4 // ex. 50.asPitchBend - > 16\cf2 \
	asPitchBend\{ \cf5 |pb = 400|\cf2  ^128 / (pb / \cf6 this\cf2 ) \} \
\
	asBark \{ \cf5 var\cf2  bk;\
		if (\cf6 this\cf2  <= 219.5) \{\
			bk = 13.3 * atan( 3 * \cf6 this\cf2  / 4000);	\cf4 // Terhardt 1979\cf2 \
		\}\{\
			bk = ( (26.81 * \cf6 this\cf2 ) / (1960 + \cf6 this\cf2 ) ) - 0.53; \cf4 // Traunmuller\cf2 \
\cf4 //			bk = (26.81 / (1 + (1960 / this)) - 0.53); // just slightly different formulation\cf2 \
			if (bk > 20.1) \{ bk = bk + (0.22 * (bk-20.1)) \}\
		\}\
		^bk\
	\}\
\
	\cf4 // this method of conversion comes from the definition of the edges of the critical bandwidth\cf2 \
	barkToFreq \{\cf5 var\cf2  barkEdge = #[0, 100, 200, 300, 400, 510, 630, 770, 920, 1080, 1270, 1480, \
			1720, 2000, 2320, 2700, 3150, 3700, 4400, 5300, 6400, 7700, 9500, 12000, 15500];\
			^barkEdge.blendAt(\cf6 this\cf2 )\
	\}\
	\
	\cf4 // this one is the inverse of the Traunmuller approximation function used in asBark\cf2 \
	\cf4 // differs from asBark below 220 hz\cf2 \
	barkToHz \{ ^1960 / (26.81 / (\cf6 this\cf2  + 0.53) - 1) \}\
	\
	\cf4 // gives the size (in Hz) of the critical bandwidth given in barks\cf2 \
	criticalBW \{ ^52548 / (\cf6 this\cf2 .squared - (52.56 * \cf6 this\cf2 ) + 690.39)\}\
\
	\cf4 // freqs to ERB (Equivalent Rectangular Bandwidth, another scale based on the CBW)\cf2 \
	\cf4 // also called ERB-rate of a tone. It is mainly used for masking analysis \cf2 \
	hzToErb \{ ^11.17 * log( (\cf6 this\cf2  + 312) / (\cf6 this\cf2  + 14675)) + 43.0\}\
	\
	hzToMel \{ ^1127.01048 * log( 1 + (\cf6 this\cf2 /700))\}\
	\
	melToHz \{ ^700 * ((\cf6 this\cf2 /1127.01048).exp -1)\}\
\
	phonToSone \{ ^2**((\cf6 this\cf2  - 40) / 10)\}\
	\
	soneToPhon \{ ^10 * (4 + (\cf6 this\cf2 .log10 / 2.log10))\}\
	\
	\cf4 // calibration: should be 0 if loudness is in dB spl; a positive number if the values\cf2 \
	\cf4 // are in dBFS or negative dB's relative to 0. \cf2 \
	\cf4 // This is the case when translating amps to db with ampdb.\cf2 \
	asPhon \{\cf5 |spl, calib = 0|\cf2  ^\cf3 LoudnessModel\cf2 .calc(\cf6 this\cf2 , spl + calib)\}\
	\
	asSone \{\cf5 |spl, calib = 0|\cf2  ^\cf6 this\cf2 .asPhon(spl, calib).phonToSone \}\
	\
	\cf4 // this is an amplitude converted into ...ppp - fff\cf2 \
	asDynamic \{\cf5 |freq = 1000, ref = 100, fff = 3|\cf2  \cf5 var\cf2  num, char;\
		num = freq.asSone(\cf6 this\cf2 .ampdb, ref).log2.round(1) - fff;\
		if (num == 0) \
			\{ \
				^\cf7 \\mf\cf2 \
			\}\{\
				if (num.isNegative) \{ char = \cf7 'p'\cf2  \} \{ char = \cf7 'f'\cf2  \};\
				^(char!num.abs).join\
			\};\
	\}\
	\
	roughness \{\cf5 |f2|\cf2  \cf5 var\cf2  dif = absdif(\cf6 this\cf2 .asBark, f2.asBark);\
		^(4 * dif * (exp(1-(4 * dif))))\
	\}\
	\
	\
\cf4 /*	\
	Bn (k) = 20log10(zeta * |Xn (k)|) where zeta = 184268 (see Nick Collins' PhD thesis p. 66)\
	zeta = 8 is usually enough for FFT values\
*/\cf2 	\
	bintodb \{\cf5 |zeta = 8|\cf2  ^( 20 * (zeta * \cf6 this\cf2 .abs).log10) \} \
\
	\cf4 // size of wavelength in meters:\cf2 \
	asWavelength  \{\cf5 |c = 343|\cf2   ^c/\cf6 this\cf2  \} \cf4 // c is speed of sound in m/s @ 20 celsius	\cf2 \
	factorial \{ \cf4 // the highest factorial that can be represented as a Float is 171\cf2 \
		^(2..\cf6 this\cf2 .asFloat).product\
	\}\
	\
\cf4 /*	Return a sequence of largest prime powers for a given harmonicity minimum. Pitch range is \
	in octaves, ex, 0.03.minHarmonicityVector(1,13) yields [12, 8, 3, 2, 1, 1]. \
	They correspond to the powers of the harmonic space bases 2,3,5,7,11,and 13 inside an octave.\
	"A maximum powers sequence includes intervals, the harmonicities of which may lie\
	below the minimum suggested [by this method]...The maximum power sequence guarantees merely\
	that all intervals that are more harmonic than a given minimum [harmonicity] value can be \
	expressed by the sequence. [12, 8, 3, 2, 1, 1] results in as many as 3,964 different intervals \
	within one octave (!), of which only 211 are truly more harmonic than 0.03" ("Two Essays on 	Theory", C.Barlow (CMJ, 1987, see formula in highestPower method below).             */\cf2  \
	minHarmonicityVector \{\cf5 |pitchRange = 1, maxPrime = 11|\cf2       \
	     ^\cf3 Array\cf2 .primes(maxPrime).collect\{\cf5 |p|\cf2  p.highestPower(\cf6 this\cf2 , pitchRange)\} \
	\}\
\
	\cf4 // this is like asFraction but hacked in order to handle rounding errors for\cf2 \
	\cf4 // harmonic interpretation of periodic decimals (0.333 will be 1/3 and not 333/1000)\cf2 \
		asRatio \{\cf5 |denominator = 100, fasterBetter = true|\cf2 \
		\cf5 var\cf2  num = \cf6 this\cf2 , str, a, b, f;\
		str = \cf6 this\cf2 .asString;\
\cf4 //		if ( (str.contains(".")) and: (str.size > 3) ) // only in pertinent cases\cf2 \
		f = str.find(\cf8 "."\cf2 );\
		if (f.notNil) \{ \
			if (str[f..].size > 2) \
				\{\
					a = str.wrapAt(-2).digit; \
					b = str.last.digit; \cf4 // get last 2 digits\cf2 \
					if ( (a == b) or: ((a + 1) == b) ) \{	\cf4 // cases like 1.33 and 1.67\cf2 \
						num = (str.drop(-1) ++ \cf8 ""\cf2 .catList(a!12)).asFloat \
					\}\
				\}\
		\};\
		^num.asFraction(denominator, fasterBetter)\
	\}\
	\
\}\
\
+ \cf3 Integer\cf2  \{\
		\
	\cf4 // Barlow's Indigestibility of an integer \cf2 \
	\cf4 // (low vs. high prime factors as a measure of "digestibility"):\cf2 \
	indigestibility \{ \cf5 var\cf2  sum = 0; \
		if ( \cf6 this\cf2  <= 1 ) \{ ^0 \};\
		\cf6 this\cf2 .factors.asBag.contents.pairsDo\{\cf5 |y0, y1|\cf2  \
			 sum = sum + ((y1 * (y0 - 1).squared) / y0)\
		\};\
		^sum * 2\
	\}\
	\cf4 // note: at prime 46349, 32-bit integer arithmetic overflows\cf2 \
	\cf4 // and gives wrong (negative) indigestibilites...\cf2 \
\
	\cf4 //  Barlow's Harmonicity formula (for an interval p/q):\cf2 \
	harmonicity \{\cf5 |q|\cf2  \
		\cf5 var\cf2  numer = \cf6 this\cf2 .indigestibility, denom = q.indigestibility;\
		^(denom - numer).sign / (numer + denom);\
	\}\
	\
	\cf4 // formula N(p) from "Two Essays on Theory", C.Barlow (CMJ, 1987): (see minHarmVector above)\cf2 \
	highestPower \{\cf5 | minHarmonicity = 1, pitchRange = 1|\cf2 \
		if (\cf6 this\cf2 .isPrime.not) \{\cf8 "Number has to be prime"\cf2 .warn; ^\cf9 nil\cf2 \};\
		if (\cf6 this\cf2  == 2) \{ \
			^((pitchRange + (minHarmonicity.reciprocal)) / \
				(1 + (256.log / 27.log))).trunc\
		\}\{\
			^((pitchRange + (minHarmonicity.reciprocal)) / \
				(\cf6 this\cf2 .indigestibility + (\cf6 this\cf2 .log / 2.log))).trunc\
		\}\
	\}\
	\
	divisorSet \{ ^(1..\cf6 this\cf2 ).select\{\cf5 |i|\cf2  (\cf6 this\cf2  / i).frac == 0\} \}\
			\
	multiples \{\cf5 |... primes|\cf2 \
		\cf5 var\cf2  factorList, multiples;\
		multiples = \cf3 Array\cf2 .newClear;\
		(2..\cf6 this\cf2 ).do\{\cf5 |i|\cf2 \
			factorList = i.factors;\
			primes.do\{\cf5 |j|\cf2 \
				factorList.occurrencesOf(j).do\{\
							factorList.remove(j)\} \};\
			if (factorList.isEmpty) \{multiples = multiples.add(i)\} \};\
		^multiples;\
	\}\
\
\cf4 /* \
 USAGE: a_number.harmonics(highest_harmonic)\
 returns an Array with the harmonics of a number up to highest\
 Ex: 5.harmonics(48) ->  [ 5, 10, 15, 20, 25, 30, 35, 40, 45 ]\
\
*/\cf2 \
	harmonics \{\cf5 |max|\cf2 \
		\cf5 var\cf2  n = 1, h = 1, result;\
		result = \cf3 Array\cf2 .newClear;\
		\{ h <= max \}.while\{ \
			h = \cf6 this\cf2  * n;\
			result = result.add(h);\
			n = n + 1;\
			\};\
		result.pop;	\
		h = result[0] ? \cf9 nil\cf2 ;\
		if (h.notNil, \{^result\}, \{^\cf9 nil\cf2 \});\
	\}\
	\
\cf4 /*\
USAGE:  a_prime.primeHarmonics(highest_harmonic)\
			returns a nested array with all the harmonics up to highest_harmonic\
			of a_prime along with all lower primes.\
			Ex.	17.primeHarmonics(20) ->\
			[ [ 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 ], [ 3, 6, 9, 12, 15, 18 ], [ 5, 10, 15, 20 ], [ 7, 14 ], [ 11 ], [ 13 ], [ 17 ] ]		\
*/\cf2 	\
	primeHarmonics \{\cf5 |maxPartial|\cf2 \
		\cf5 var\cf2  primeList, result = \cf3 Array\cf2 .newClear;\
		if (\cf6 this\cf2 .isPrime.not) \{\cf8 "Number has to be prime"\cf2 .warn; ^\cf9 nil\cf2 \};\
		primeList = \cf3 Array\cf2 .primes(\cf6 this\cf2 );\
		result = primeList.collect(\{\cf5 |i|\cf2  i.harmonics(maxPartial)\}); \
		result.removeAllSuchThat(\{\cf5 |n|\cf2  n.isNil\});\
		^result;\
	\} \
\
\cf4 /*	\
 USAGE: a_prime.listOfHarmonics(highest_harmonic)\
		returns an array with all the harmonics (up to highest) of \
		all the primes below (and including) a_prime\
		Ex.  7.listOfHarmonics(50)	\
 this is equivalent to: highest_harmonic.multiples(array of primes up to a_prime)\
*/\cf2 	\
	listOfHarmonics \{\cf5 |max|\cf2 \
		\cf5 var\cf2  result;\
		result = \cf6 this\cf2 .primeHarmonics(max);\
		result = result.flatten;\
		result = result.sort;\
		result.removeAllSuchThat(\{\cf5 |n|\cf2  result.occurrencesOf(n) > 1\});\
		^result\
	\}\
	\
	vpNumbers \{\cf5 |primeArray|\cf2  ^primeArray.collect\{\cf5 |n|\cf2  n.harmonics(n * \cf6 this\cf2 ) \} \}\
\}\
\
+ \cf3 SequenceableCollection\cf2  \{\
\cf4 //  instead make a Ratio class!!\cf2 \
	\
\cf4 //	The following three methods deal with rational numbers, expressed as [p,q] arrays: \cf2 \
\
	\
	\cf4 // rational division: [p,q] / [r,s]. ex: [5,9].ratioDiv([2,6]) -> [5,3]\cf2 \
	ratioDiv \{\cf5 |that, reduce = true|\cf2  \
		\cf5 var\cf2  div = [ \cf6 this\cf2 [0] * that[1], \cf6 this\cf2 [1] * that[0] ];\
		if (reduce) \{^div.reduceRatio\}\{^div\}\
	\}\
	\
	ratioMul \{\cf5 |that|\cf2 \
		^[\cf6 this\cf2 [0] * that[0], \cf6 this\cf2 [1] * that[1]].reduceRatio	\} \
	\
	ratioAdd \{\cf5 |that|\cf2  \cf5 var\cf2  denom, sum;\
		if (\cf6 this\cf2 [1] != that[1]) \{\
			denom = lcm(\cf6 this\cf2 [1],that[1]);\
			sum = (\cf6 this\cf2 [1] * that[0]) + (that[1] * \cf6 this\cf2 [0])\
		\}\{\
			sum = \cf6 this\cf2 [0] + that[0];\
			denom = \cf6 this\cf2 [1];\
		\}\
		^[sum, denom].reduceRatio\
	\} \
\
	ratioSub \{\cf5 |that|\cf2  \cf5 var\cf2  denom, diff;\
		if (\cf6 this\cf2 [1] != that[1]) \{\
			denom = lcm(\cf6 this\cf2 [1],that[1]);\
			diff = (that[1] * \cf6 this\cf2 [0]) - (\cf6 this\cf2 [1] * that[0]);\
		\}\{\
			diff = \cf6 this\cf2 [0] - that[0];\
			denom = \cf6 this\cf2 [1];\
		\};\
		^[diff, denom].reduceRatio\
	\} \
	\
\cf4 // ok	\cf2 \
	\
		\
	\cf4 // express a rational as [p,q] where p and q are coprime:\cf2 \
	reduceRatio \{ ^\cf6 this\cf2  div: (\cf6 this\cf2 [0] gcd: \cf6 this\cf2 [1]) \}\
\
	reduceOctave \{ ^\cf6 this\cf2 .asHvector.collect\{\cf5 |x|\cf2   x.reducedRatio.as(\cf3 Array\cf2 ) \} \}		\
		\
\cf4 //			var fratio = x[0]/x[1], pow2 = 0, res = x.copy;\cf2 \
\cf4 //			\{ fratio < 1 \}.while\{ fratio = fratio * 2; pow2 = pow2 + 1 \};\cf2 \
\cf4 //			\{ fratio >= 2 \}.while\{ fratio = fratio / 2; pow2 = pow2 - 1 \};\cf2 \
\cf4 //			if (pow2.isNegative) \{ \cf2 \
\cf4 //				res[1] = (res[1] * ( 2**pow2.abs )).asInteger\cf2 \
\cf4 //			\}\{\cf2 \
\cf4 //				res[0] = (res[0] * ( 2**pow2 )).asInteger\cf2 \
\cf4 //			\}; \cf2 \
\cf4 //			res\cf2 \
\cf4 //		\}\cf2 \
\cf4 //	\}\cf2 \
\
	ratioPost \{\cf5 |char = ", "|\cf2  \
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \{\
			^\cf6 this\cf2 [0].asString ++ \cf8 "/"\cf2  ++ \cf6 this\cf2 [1].asString\
		\}\{	\
			^\cf6 this\cf2 .collect\{\cf5 |d|\cf2  d[0].asString ++ \cf8 "/"\cf2  ++ d[1].asString\}.join(char)\
		\}\
	\}	\
	\
	\
	\cf4 // sort an array of [p,q] ratios\cf2 \
	ratioSort \{ ^\cf6 this\cf2 [\cf6 this\cf2 .ratioToFreq.order] \}\
	\
	\
	\cf4 // lowest common denominator\cf2 \
	lcd \{ ^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x[1] \}.reduce(\cf7 \\lcm\cf2 ) \}\
	\
	\
	\cf4 // adapted from John Chalmers' "Divisions of the Tetrachord", chapter 2\cf2 \
	katapykne \{\cf5 |n = 1|\cf2  \cf5 var\cf2  ints = ((\cf6 this\cf2 [0] * n)..(\cf6 this\cf2 [1] * n)).reverse;\
		^(ints.size - 1).collect\{\cf5 |i|\cf2 \
			[ ints[i+1], ints[i] ]\
		\}.reverse\
	\}\
	\
	katapykne_ab \{\cf5 |a = 1, b = 2|\cf2  \
		^[\
			[(a+b) * \cf6 this\cf2 [0], (b*\cf6 this\cf2 [0]) + (a*\cf6 this\cf2 [1])].reduceRatio,\
			[(b*\cf6 this\cf2 [0]) + (a*\cf6 this\cf2 [1]), (a+b) * \cf6 this\cf2 [1]].reduceRatio\
		]\
	\}\
	\
	ratioToFreq \{\cf5 |fundFreq = 1|\cf2 \
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) )\
			\{\
				^((\cf6 this\cf2 [0]/\cf6 this\cf2 [1]) * fundFreq)\
			\}\{\
				^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.ratioToFreq * fundFreq \} \
			\}\
	\}		\
\
	\cf4 // converting pitch sets from absoulte intervals to adjacency intervals\cf2 \
	ratioDifferentiate \{ \
		\cf5 var\cf2  list, prev = [1,1];\
		list = \cf6 this\cf2 .class.new(\cf6 this\cf2 .size);\
		\cf6 this\cf2 .do\{\cf5 |x|\cf2 \
			list.add(x.ratioDiv(prev));\
			prev = x;\
		\};\
		^list\
	\}\
	\
	\cf4 // from adjecency to absolute intervals: \cf2 \
	ratioIntegrate \{ \cf5 var\cf2  res, list, prev = [1,1];\
		list = \cf6 this\cf2 .class.new(\cf6 this\cf2 .size);\
		\cf6 this\cf2 .do\{\cf5 |x|\cf2  \
			res = (x * prev).reduceRatio;\
			list.add(res);\
			prev = res;\
		\};\
		^list\
	\}\
	\
	ratioSum \{ ^\cf6 this\cf2 .product.reduceRatio \}\
	\
	ratioDifference \{ \cf5 var\cf2  prev = [1,1];\
		^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.ratioDiv(prev); prev = x\}.sum.reduceRatio;\
	\}\
	\
	\cf4 // must be an array of ratios. The collection of ratios will be converted as a group to harms.\cf2 \
	\cf4 //	Ex. [[1,1],[16,15],[6,5],[4,3],[3,2],[8,5],[9,5],[2,1]].ratioToHarmonics ->\cf2 \
	\cf4 //	[ 30, 32, 36, 40, 45, 48, 54, 60 ]		\cf2 \
	ratioToHarmonics \{ \cf5 var\cf2  numerator, denominator;\
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \{\
			numerator = \cf6 this\cf2 [0]; \
			denominator = \cf6 this\cf2 [1];\
			^[numerator.lcm(denominator).div(denominator)]\
		\}\{\
			numerator = \cf6 this\cf2 .collect\{\cf5 |x|\cf2  x[0]\}; \
			denominator = \cf6 this\cf2 .collect\{\cf5 |x|\cf2  x[1]\};\
			^(numerator * denominator.reduce(\cf7 \\lcm\cf2 )).div(denominator);\
		\};\
		\
	\}\
	\
	\cf4 // must be an array of whole numbers\cf2 \
	\cf4 // ex. [24,27,30,32,36,40,45,48].harmonicsToRatios ->\cf2 \
	\cf4 //					 [[1,1],[9,8],[5,4],[4,3],[3,2],[5,3],[15,8],[2,1]]\cf2 \
\cf4 /*	harmonicsToRatios	\{ var ratios = this.collect\{|x| [x,1]\};\
		^ratios.collect\{|x| x.ratioDiv([this.minItem, 1])\}\
	\}\
*/\cf2 \
	\
\cf4 //	new version: make ratios with the smallest harmonic and reduce\cf2 \
	harmonicsToRatios \{ ^[\cf6 this\cf2 ,\cf6 this\cf2 .minItem].flop.collect(\cf3 _\cf2 .reduceRatio) \}\
	\
\cf4 /*	subharmonicsToRatios \{var ratios = this.collect\{|x| [1,x]\};\
		^ratios.collect\{|x| x.ratioDiv([1,this.maxItem])\}\
	\}\
*/\cf2 \
	subharmonicsToRatios \{ ^[\cf6 this\cf2 .maxItem, \cf6 this\cf2 ].flop.collect(\cf3 _\cf2 .reduceRatio)\}\
	\
	arithmeticMean \{ ^(\cf6 this\cf2 .sum / \cf6 this\cf2 .size) \}\
	\
	harmonicMean \{ ^(\cf6 this\cf2 .size / \cf6 this\cf2 .reciprocal.sum) \}\
	\
	geometricMean \{ ^( \cf6 this\cf2 .reduce(\cf7 '*'\cf2 )**(1/\cf6 this\cf2 .size)) \}\
	\
	\cf4 // integer means: \cf2 \
	intArithmeticMean \{\
		\cf5 var\cf2  ratio = [\cf6 this\cf2 [0] * 2, \cf6 this\cf2 [1] * 2], \
		res = [ ratio[0], ratio.mean.asInteger, ratio[1] ].reverse.harmonicsToRatios;\
		^res[1..].ratioDifferentiate\
	\}\
	\
	intHarmonicMean \{ ^\cf6 this\cf2 .intArithmeticMean.reverse \}\
\
\cf4 // Novaro arithmetic progression	\cf2 \
	novaroSeries \{\cf5 |n = 1|var\cf2  p,q;\
		#p,q  = \cf6 this\cf2 ;\
		^\cf3 Array\cf2 .series(n+2, min(p,q) * (n+1), (p-q).abs)\
	\}\
	\
\cf4 // Novaro fundamental scale. 'this' is a ratio [p,q]\cf2 \
	novaroF \{\cf5 |n = 1|\cf2  ^\cf6 this\cf2 .novaroSeries(n).harmonicsToRatios \}\
	\
\cf4 // Novaro reciprocal scale\cf2 \
	novaroR \{\cf5 |n = 1|\cf2  \
		^\cf6 this\cf2 .novaroF(n).collect\{\cf5 |x|\cf2  \
			[x[1] * \cf6 this\cf2 [0], x[0] * \cf6 this\cf2 [1]].reduceRatio \
		\}.ratioSort\
	\}\
	\
\cf4 //Novaro gradual scale (with a cofundamental)\cf2 \
	novaroG\{\cf5 |n = 1, cofund = #[3,2]|\cf2 \
		^\cf6 this\cf2 .novaroF(n).collect\{\cf5 |x|\cf2 \
			[ x[1] * cofund[0], x[0] * cofund[1] ].reduceRatio\
		\}.ratioSort\
	\}\
	\
\cf4 // Novaro complex scale\cf2 \
	novaroC\{\cf5 |n = 1, cofund = #[3,2]|\cf2 \
		^(\cf6 this\cf2 .novaroF(n) ++ \cf6 this\cf2 .novaroR(n)).removeDuplicates.ratioSort\
	\}\
	\
	novaroPosition \{\cf5 |pos = 2|\cf2 \
		^(\cf6 this\cf2 [(pos-1)..] \
		++ (pos-1).collect\{\cf5 |x|\cf2  (\cf6 this\cf2 .last * \cf6 this\cf2 [x+1]).reduceRatio\})\
	\}\
	\
	novaroPositionTransp \{\cf5 |pos = 2|\cf2  \cf5 var\cf2  posicion, first;\
		posicion = \cf6 this\cf2 .novaroPosition(pos);\
		first = posicion.first;\
		^posicion.collect\{\cf5 |x|\cf2  x.ratioDiv(first)\}\
	\}\
	\
	novaroChordPositions \{\
		^(\cf6 this\cf2 .size-1).collect\{\cf5 |x|\cf2  \cf6 this\cf2 .novaroPositionTransp(x+1)\}\
	\}\
	\
	\cf4 // see Novaro, p. 43\cf2 \
	novaroChordSystem \{\cf5 |n = 1|\cf2  \cf5 var\cf2  fund, recip;\
		fund = \cf6 this\cf2 .novaroF(n).novaroChordPositions;\
		recip = \cf6 this\cf2 .novaroR(n).novaroChordPositions;\
		recip = [recip[0]] ++ recip[1..].reverse;\
		^fund.collect\{\cf5 |x,i|\cf2  [x,recip[i]]\}.flatten(1);\
	\}		\
\
				\
\cf4 //	several methods designed to work with arrays of pairs (either rationals or [', spl]):\cf2 \
\
	\cf4 // phon values for [freq, spl] pairs: \cf2 \
	asPhon \{\cf5 |calib = 0|\cf2  \cf5 var\cf2  res;\
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isSequenceableCollection.not))\
			\{\
				res = \cf6 this\cf2 [0].asPhon(\cf6 this\cf2 [1], calib);\
			\}\{\
				res = \cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.asPhon(calib) \}\
			\};\
		^res\
	\}\
\
	\cf4 // just a convenience for not writing asPhon.phonToSone:\cf2 \
	asSone \{ \cf5 |calib = 0|\cf2  \cf5 var\cf2  res;\
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isSequenceableCollection.not))\
			\{\
				res = \cf6 this\cf2 [0].asSone(\cf6 this\cf2 [1], calib);\
			\}\{\
				res = \cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.asSone(calib) \}\
			\};\
		^res\
	\}\
		\
	\cf4 // Convenience method from LoudnessModel. Returns the amplitudes of partials after masking, \cf2 \
	\cf4 // should be in the form of [freq, spl] pairs:\cf2 \
	compensateMasking \{ \cf5 |gradient = 12|\cf2  \cf5 var\cf2  f, res;\
		f = \cf6 this\cf2 .flop;\
		^\cf3 LoudnessModel\cf2 .compensateMasking(f[0], f[1], gradient);\
	\}		\
\
	\cf4 // cents value for rationals [p,q]:\cf2 \
	cents \{\
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \
			\{\
				^\cf6 this\cf2 [0].cents(\cf6 this\cf2 [1]);\
			\}\{\
				^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.cents \})\
			\};\
	\}\
	\
	\cf4 // Barlow's harmonicity (see above for formula) for arrays of rational pairs [p,q]:\cf2 \
	harmonicity \{\cf5 |clean = true|\cf2  \cf5 var\cf2  res; \
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \
			\{\
				res = \cf6 this\cf2 [1].harmonicity(\cf6 this\cf2 [0]);\
				if (clean) \{ if (res.isNaN) \{ res = 2 \}\}; \cf4 // replace harmonicity of 1/1 with 2\cf2 \
												   \cf4 // instead of nan\cf2 \
			\}\{\
				res = \cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.harmonicity(clean) \})\
			\};\
		^res\
	\}	\
\
	\cf4 // James Tenney's harmonic distance (city-block metric of harmonic lattices)\cf2 \
	\cf4 // for arrays of rational pairs [p,q] (see Tenney[1983]):\cf2 \
\
	harmonicDistance \{ \
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \{\
				^(\cf6 this\cf2 [1] * \cf6 this\cf2 [0]).log2;\
			\}\{\
				^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.harmonicDistance \}\
			\};\
	\}\
	\
	pitchDistance \{\
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \{\
				^(max(\cf6 this\cf2 [0], \cf6 this\cf2 [1]) / min(\cf6 this\cf2 [0], \cf6 this\cf2 [1])).log2;\
			\}\{\
				^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.pitchDistance \}\
			\};\
	\}\
	\
	\cf4 // L. Euler's Gradus Suavitatis (1739, 'degree of sweetness'), arrays of rational pairs [p,q]:\cf2 \
	\cf4 // G(a) = 1 + k1*(p1-1) + k2*(p2-1) +.... + kn * (pn-1)\cf2 \
	\cf4 // where a = (p1^k1) *(p2^k2)*...*(pn^kn) and p1, p2, ... pn are its prime factors\cf2 \
	gradusSuavitatis \{ \
		if ( (\cf6 this\cf2 .size == 2) and: (\cf6 this\cf2 [0].isNumber) ) \{\
				^(\cf6 this\cf2 [0] * \cf6 this\cf2 [1]).factors.collect\{\cf5 |x|\cf2  x - 1\}.sum + 1\
			\}\{\
				^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.gradusSuavitatis \}\
			\};\
	\}\
	\
	\cf4 // returns the gradus suavitatis of a scale or chord\cf2 \
	gradusSuavitatisN \{ ^[\cf6 this\cf2 .ratioToHarmonics.reduce(\cf7 \\lcm\cf2 ).abs, 1].gradusSuavitatis\}\
	\
	\cf4 // this is an array of freqs\cf2 \
	chordRoughness\{\
		\cf5 var\cf2  dissonance = 0, roughness;\
		\cf6 this\cf2 .size.do\{\cf5 |i|\cf2 \
			\cf6 this\cf2 .size.do\{\cf5 |j|\cf2 \
				if (i < j) \{\
					roughness = \cf6 this\cf2 [i].roughness(\cf6 this\cf2 [j]);\
					dissonance = dissonance + roughness;\
\
				\};\
			\};\
		\};\
		^dissonance\
	\}\
	\
	\cf4 // this is an array of freqs with an amps argument which is an array of amps\cf2 \
	chordDissonance\{\cf5 |amps, calib = 100, compensate = true, gradient = 20|\cf2  \
		\cf5 var\cf2  sones, dissonance = 0, roughness;\
		if (compensate) \{\
		  	sones = [\cf6 this\cf2 , [\cf6 this\cf2 , amps.ampdb+calib].flop.compensateMasking(gradient)].\
		  		flop.asSone(0);\
		\}\{\
			sones = [\cf6 this\cf2 , amps.ampdb].flop.asSone(calib);\
		\};\
		\cf6 this\cf2 .size.do\{\cf5 |i|\cf2 \
			\cf6 this\cf2 .size.do\{\cf5 |j|\cf2 \
				if (i < j) \{\
					roughness = sqrt(sones[i] * sones[j]) * \cf6 this\cf2 [i].roughness(\cf6 this\cf2 [j]);\
					dissonance = dissonance + roughness;\
				\};\
			\};\
		\};\
		^dissonance\
	\}\
\
	harmonicityBetween\{\}\
	\
\cf4 // the following are methods for SimpleNumber made to work in SequenceableCollection:\cf2 \
\
	asNote \{^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.asNote\})\}\
	\
	asMidi \{^\cf3 NoteNames\cf2 .table.atAll(\cf6 this\cf2 ) \}\
\
	addCents \{\cf5 |cents|\cf2  ^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.addCents(cents)\})\}\
	\
	asBark \{^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.asBark\})\}\
	\
	barkToFreq \{^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.barkToFreq \} \}\
	\
	barkToHz \{^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.barkToHz \} \}\
	\
	criticalBW \{^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.criticalBW \} \}\
	\
	hzToErb \{^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.hzToErb \} \}\
	\
	hzToMel \{^\cf6 this\cf2 .collecy\{\cf5 |x|\cf2  x.hzToMel\} \}\
	\
	melToHz \{^\cf6 this\cf2 .collecy\{\cf5 |x|\cf2  x.melToHz\} \}\
	\
	phonToSone \{^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.phonToSone \} \}\
	\
	soneToPhon \{^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.soneToPhon \} \}\
	\
	asDynamic\{\cf5 |freq = 1000, ref = 100, fff = 3|\cf2  \
		if (freq.isSequenceableCollection) \{\
				^\cf6 this\cf2 .collect\{\cf5 |x, i|\cf2  x.asDynamic(freq[i], ref, fff) \}\
		\}\{\
			^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.asDynamic(freq, ref, fff) \} \
		\}\
	\}\
	\
	asRatio \{\cf5 |denom = 100, fasterBetter = false|\cf2  \
		^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.asRatio(denom, fasterBetter)\}\
	\}\
	\
	vpChord \{\cf5 |aprox = 1, prime = 7, max = 24|\cf2  \
		^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.vpChord(aprox, prime, max)\}).flatten\
	\}\
\
	vpChordClosed \{\cf5 |aprox = 1, prime = 7, max = 24|\cf2 \
			^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.vpChordClosed(aprox, prime, max)\}).flatten\
	\}\
\
	vpChordOpen \{\cf5 |aprox = 1, prime = 7, max = 24|\cf2 \
			^\cf6 this\cf2 .collect(\{\cf5 |x|\cf2  x.vpChordOpen(aprox, prime, max)\}).flatten\
	\}\
	\
	cents2Frq \{\cf5 |frq = 1|\cf2  ^\cf6 this\cf2 .collect\{\cf5 |x|\cf2  x.cents2Frq(frq)\}  \}\
	\
	centsToName \{\cf5 |tolerance = 12, restore = true|\cf2 \
		\cf5 var\cf2  g,table = \cf3 IntervalTable\cf2 .tableType, \
		min = \cf3 IntervalTable\cf2 .tableMin,\
		change = \cf9 false\cf2 ;\
		if (table != \cf7 \\huygens\cf2 ) \{\cf3 IntervalTable\cf2 .loadTable(\cf7 \\huygens\cf2 ); change = \cf9 true\cf2 \};\
		g = \cf3 IntervalTable\cf2 .classify(\cf6 this\cf2 , tolerance);\
		\cf6 this\cf2 .do\{\cf5 |int, indx|\cf2 \
		  postf(\cf8 "The following intervals are close to % cents by +/- % cents:\\n"\cf2 , int, tolerance);\
		  g[indx].do\{\cf5 |x|\cf2 \
		  	postf(\cf8 "\\t%/%,\\t% cents,\\t%\\n"\cf2 ,x[0][0],x[0][1],x[1].round(0.001),x[2]);\
		  \};\
		  \cf8 ""\cf2 .postln;\
		 \};\
		if (restore) \{\
			if (change) \{\cf3 IntervalTable\cf2 .loadTable(table, min)\};\
		\}\
	\} \
	\
	\cf4 // falta que funcione para cents y ratios simples, no solo en arrays...\cf2 \
	ratioToName \{\cf5 |tolerance = 16, restore = true|\cf2  ^\cf6 this\cf2 .cents.centsToName(tolerance, restore)\}\
	\
	\cf4 // a shortcut for making arrays of primes:	\cf2 \
	\cf4 // ex. Array.primes(11) -> [2, 3, 5, 7, 11]\cf2 \
	*primes \{\cf5 |maxPrime = 11|\cf2  \cf5 var\cf2  obj = \cf6 this\cf2 .newClear, i = 0;\
		\{ i < maxPrime\}.while\{ \
	     	i = i + 1; \
	     	if (i.isPrime) \{obj = obj.add(i)\}\
     	\};\
		^obj\
	\}\
\
		\
	analyseScale \{\cf5 |tolerance = 16, type = \\size, maxNum = 729, maxDenom = 512, maxPrime = 31, post = true|\cf2 \
		\cf5 var\cf2  classification, res, cents = \cf6 this\cf2 .cents;\
		classification = \cf3 IntervalTable\cf2 .classify(cents, tolerance);\
		type.switch(\
			\cf7 \\size\cf2 , \{ \cf4 // reduce by size of num and denom\cf2 \
				res = classification.collect\{\cf5 |x,i|\cf2 \
					x.reject\{\cf5 |y|\cf2  (y[0][1] > maxDenom) or: (y[0][0] > maxNum) \};\
				\}\
			\},\
			\cf7 \\prime\cf2 , \{ \cf4 // reduce by max prime factor\cf2 \
				res = classification.collect\{\cf5 |x,i|\cf2 \
						x.reject\{\cf5 |y|\cf2  \
							\cf5 var\cf2  p = y[0][0].factorsHarmonic, q = y[0][1].factorsHarmonic;\
\cf4 /*							((p.maxItem.isNil) or: (q.maxItem.isNil)) or: \{*/\cf2 \
								(p.maxItem > maxPrime) or: (q.maxItem > maxPrime)  \
\cf4 /*							\}*/\cf2 \
						\}\
				\}\
			\},\
			\{^\cf8 "Invalid type!"\cf2 \}\
		);			\
		res = res.collect\{\cf5 |x, i|\cf2  if( (x == []) or: (x.isNil) )\
					\{[[\cf6 this\cf2 [i], cents[i], \cf8 "NO MATCH"\cf2 ]]\} \{x\} \};\
		if (post) \{\
			postf(\cf8 "Each scale degree is close to the following intervals by +/-% cents.\\n"\cf2 , tolerance);\
			type.switch(\cf7 \\size\cf2 , \{postf(\cf8 "Max denominator: %, max numerator: %\\n"\cf2 , maxDenom, maxNum)\},\
						\cf7 \\prime\cf2 , \{postf(\cf8 "Max prime: %\\n"\cf2 , maxPrime)\});\
			res.do\{\cf5 |x, i|\cf2 \
				postf(\cf8 "%> % ( % cents)  ------------------------------\\n"\cf2 , \
					i+1, \cf6 this\cf2 [i], cents[i].round(0.001) );\
				x.do\{\cf5 |y|\cf2  postf(\cf8 "\\t\\t\\t\\t %/%, % cents, %\\n"\cf2 , y[0][0], y[0][1], y[1].round(0.001), y[2])\};\
				\};\
			^\cf8 ""\cf2 	\
		\}\
		^res;\
	 \}		\
	 \
\
\cf4 // search for a way to favor things like 12/11 instead of 35/32 (answer: harmonicDistance)\cf2 \
	 rationalize \{\cf5 |tolerance = 16, metric, type = \\size, max|\cf2 \
	 	\cf5 var\cf2  candidates, res, harms, maxNum, maxDenom, maxPrime;\
	 	if (metric.class != \cf3 HarmonicMetric\cf2 ) \{metric = \cf3 HarmonicMetric\cf2 (metric)\};\
	 	type.switch(\
	 		\cf7 \\size\cf2 , \{\
		 		max = max ? [729, 512]; \
		 		maxNum = max[0]; \
		 		maxDenom = max[1]\
		 	\},\
	 		\cf7 \\prime\cf2 , \{\
		 		max = max ? 19; \
		 		maxPrime = max;\
		 	\}\
	 	);\
	 	candidates = \cf6 this\cf2 .analyseScale(tolerance, type, maxNum, maxDenom, maxPrime, \cf9 false\cf2 );\
	 	res = candidates.collect\{\cf5 |x|\cf2 \
			if (x.size == 1) \{	\
				x[0][0]\
			\}\{\
				harms = x.collect\{\cf5 |y|\cf2  y[0]\};	\
				metric.mostHarmonic(harms)\
			\};\
		\};\
		^res;\
	 \}\
	 \
\}\
\
+ \cf3 Collection\cf2  \{\
\
	removeDuplicates \{ ^\cf6 this\cf2 .asSet.perform( (\cf7 'as'\cf2  ++ \cf6 this\cf2 .class).asSymbol ) \}\
	\
	asFloatArray \{^\cf3 FloatArray\cf2 .new(\cf6 this\cf2 .size).addAll(\cf6 this\cf2 ) \}\
\}\
\
+ \cf3 SimpleNumber\cf2  \{\
\
\cf4 // variations on Virtual Pitch chords\cf2 \
	vpChord \{\cf5 |aprox = 1, primeArray, maxMultiple = 3|\cf2  \
		\cf5 var\cf2  t, new = \cf3 Array\cf2 .newClear;\
		t = maxMultiple.vpNumbers(primeArray);\
		new = new.add(t[0].choose);\
		t.remove(t[0]);\
		t.do\{\cf5 |x|\cf2  \
			\cf5 var\cf2  temp; temp = x.choose;\
			while \{new.includes(temp)\} \{temp = x.choose\};\
			new = new.add(temp);\
		\};\
		^(new.sort * \cf6 this\cf2 .midicps).cpsmidi.round(aprox);\
	\}\
	\
	vpChordClosed \{\cf5 |aprox = 1, primeArray, maxMultiple = 3|\cf2 \
		\cf5 var\cf2  t, new = \cf3 Array\cf2 .newClear;\
		t = maxMultiple.vpNumbers(primeArray);\
		new = new.add(t[0].choose);\
		t.remove(t[0]);\
		(t.size).do \{\cf5 |i|\cf2 \
			\cf5 var\cf2  temp; temp = new[i].nearestInList(t[i]);\
			while \{new.includes(temp)\} \{temp = t[i].choose\};\
			new = new.add(temp);\
		\};\
		^(new.sort * \cf6 this\cf2 .midicps).cpsmidi.round(aprox);\
	\}\
	\
	vpChordOpen \{\cf5 |aprox = 1, primeArray, maxMultiple = 3|\cf2 \
		\cf5 var\cf2  t, new = \cf3 Array\cf2 .newClear;\
		t = maxMultiple.vpNumbers(primeArray);\
		new = new.add(t[0].choose);\
		t.remove(t[0]);\
		(t.size).do\{\cf5 |i|\cf2 \
			\cf5 var\cf2  temp; \
			temp = t[i].maxItem(\{\cf5 |x|\cf2  (x-new[i]).abs\});\
			while \{new.includes(temp)\} \{temp = t[i].choose\};\
			new = new.add(temp);\
		\};\
		^(new.sort * \cf6 this\cf2 .midicps).cpsmidi.round(aprox);\
	\}\
\
\}\
\
\cf4 // jsl: 2005-2008	\cf2 \
\cf4 /*	\
	TO DO: indispensibility for meters\
*/\cf2 \
+ \cf3 Integer\cf2  \{\
	factorsHarmonic \{\
		\cf5 var\cf2  num, array, prime;\
\cf4 // the reason to hack this into a mathematically incorrect factorization is beacuse it \cf2 \
\cf4 // saves a ton of work dealing with prime filtering in harmonic ratios:\cf2 \
\cf4 //		if(this <= 1) \{ ^[] \}; // no prime factors exist below the first prime\cf2 \
		num = \cf6 this\cf2 .abs;\
		\cf4 // there are 6542 16 bit primes from 2 to 65521\cf2 \
		6542.do \{\cf5 |i|\cf2 \
			prime = i.nthPrime;\
			while \{ (num mod: prime) == 0 \}\{\
				array = array.add(prime);\
				num = num div: prime;\
				if (num == 1) \{^array\}\
			\};\
			if (prime.squared > num) \{\
				array = array.add(num);\
				^array\
			\};\
		\};\
		\cf4 // because Integer is 32 bit, and we have tested all 16 bit primes,\cf2 \
		\cf4 // any remaining number must be a prime.\cf2 \
		array = array.add(num);\
		^array\
	\}\
\}}