+ SimpleNumber {
    asNote { var residue, octave, note, roundedNote;
        note = this.cpsmidi; 
        roundedNote = note.round(1);
        octave = ((roundedNote / 12).asInteger) - 1;
        residue  = (note.frac * 100).round(1);
        if (residue > 50) {
            ^[NoteNames.flatnames[(roundedNote - 72) % 12].asString ++ octave.asString, 
                (100 - residue).neg]
        }{
            ^[NoteNames.names[(roundedNote - 72) % 12].asString ++ octave.asString, 
                residue];
        }
    }

    factorial { // the highest factorial that can be represented as a Float is 171
		^(2..this.asFloat).product
	}
}
