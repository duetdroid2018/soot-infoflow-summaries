package soot.jimple.infoflow.methodSummary.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.xml.ISummaryWriter;
import soot.jimple.infoflow.methodSummary.xml.WriterFactory;

public class HandleException {
	private static final Logger logger = LoggerFactory.getLogger(SummaryGenerator.class);

	public static void handleException(MethodSummaries flows, String file, String folder,
			Exception e, String msg) {
		System.err.println("########## Start handle exception #############");

		FileWriter fw = null;
		try {
			fw = new FileWriter(file + "_exception_" + System.currentTimeMillis() + ".txt");
		} catch (IOException e1) {
		}
		PrintWriter pw = new PrintWriter(fw);
		
		e.printStackTrace(pw);
		e.printStackTrace();
		pw.append("msg: " + msg + "\n\n");
		pw.close();
		try {
			fw.close();
		} catch (IOException e1) {
		}
		write(flows, System.currentTimeMillis() + "_fail_" + file, folder);
		System.err.println("########## end handle exception #############");
	}

	public static void handleException(String filePrefix, String msg, Exception e) {
		System.err.println("########## Start handle exception #############");

		FileWriter fw = null;
		try {
			fw = new FileWriter(filePrefix + "_exception_" + System.currentTimeMillis() + ".txt");
		} catch (IOException e1) {
		}
		PrintWriter pw = new PrintWriter(fw);
		e.printStackTrace(pw);
		e.printStackTrace();
		pw.append("msg: " + msg + "\n\n");
		pw.close();
		try {
			fw.close();
		} catch (IOException e1) {
		}
		System.err.println("########## end handle exception #############");
	}

	private static void write(MethodSummaries flows, String fileName, String folder) {
		ISummaryWriter writer = WriterFactory.createXMLWriter(fileName, folder);
		try {
			writer.write(flows);
		} catch (XMLStreamException e) {
			logger.error("failed to writer flows");
		}
	}

}
