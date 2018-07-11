package edu.pku.sei.codeclone.predictor.rcf;

import java.io.File;
import java.io.FileNotFoundException;

import de.uni_bremen.st.rcf.model.RCF;
import de.uni_bremen.st.rcf.persistence.AbstractPersistenceManager;
import de.uni_bremen.st.rcf.persistence.PersistenceManagerFactory;

public class RCFLoader {
	public static RCF loadRCF(String rcfFile) throws FileNotFoundException{
		File file = new File(rcfFile);
		AbstractPersistenceManager apm = PersistenceManagerFactory.getPersistenceManager(file);
		RCF data = apm.load(file);
		return data;
	}
}
