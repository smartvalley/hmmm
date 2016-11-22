package ru.mipt.edf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class EDFKirjoittaja {
		// Testataan!!!
		// Tämä ei toimi EDFbrowserissa???
		public static void main(String[] args) {
			EDFHeader header = EDFKirjoittaja.buildHeader(
					new Date(), new Integer(100), new Integer[]{10000, 10000}, "Patient",
					new Boolean(true), new Date(), 
					"Seppo", "Töölö", "Mikko", "Älykello", 
					new String[]{"Ch1", "Ch2"}, 
					new String[]{"Trsd1", "Trsd2"},
					new String[]{"Dim1", "Dim2"}, 
					new Double[]{100.0, 100.0}, new Double[]{-100.0, -100.0}, 
					new Integer[]{-100, -100}, new Integer[]{100, 100}, 
					new String[]{"Pf1", "Pf2"}
			);
			EDFHeader header2 = EDFKirjoittaja.buildHeader2();
			write("testi.edf", header2);
		}
	 public static EDFHeader buildHeader2() {
             return new EDFAnnotationFileHeaderBuilder()
                     .startOfRecording(new Date()).durationOfRecord(100).numberOfSamples(new Integer[] { 10000 })
                     .patientCode("1234").patientIsMale(true).patientBirthdate(new Date()).patientName("The patient")
                     .recordingHospital("Hosp.").recordingTechnician("Techn.").recordingEquipment("Equ.")
                     .channelLabels(new String[] { "EDF Annotations" }).transducerTypes(new String[] { "" })
                     .dimensions(new String[] { "" }).minInUnits(new Double[] { 0.0 }).maxInUnits(
                             new Double[] { 1.0 })
                     .digitalMin(new Integer[] { -32768 }).digitalMax(new Integer[] { 32767 })
                     .prefilterings(new String[] { "" }).numberOfSamples(new Integer[] { 10000 })
                     .reserveds(new byte[1][EDFConstants.RESERVED_SIZE]).build();
     }
	/*
	 * Rakentaa EDFHeaderin annetuista parametreista
	 */
	public static EDFHeader buildHeader(
			Date startOfRecording, Integer durationOfRecord, Integer[] numberOfSamples,
			String patientCode, Boolean patientIsMale, Date patientBirthDate,
			String patientName, String recordingHospital,String recordingTechnician,
			String recordingEquipment, String[] channelLabels, String[] transducerTypes,
			String[] dimensions, Double[] minInUnits, Double[] maxInUnits,
			Integer[] digitalMin, Integer[] digitalMax, String[] prefilterings) {
		EDFHeader header = new EDFAnnotationFileHeaderBuilder()
				.startOfRecording(startOfRecording).durationOfRecord(durationOfRecord).numberOfSamples(numberOfSamples)
                .patientCode(patientCode).patientIsMale(patientIsMale).patientBirthdate(patientBirthDate).patientName(patientName)
                .recordingHospital(recordingHospital).recordingTechnician(recordingTechnician).recordingEquipment(recordingEquipment)
                .channelLabels(channelLabels).transducerTypes(transducerTypes)
                .dimensions(dimensions).minInUnits(minInUnits).maxInUnits(maxInUnits)
                .digitalMin(digitalMin).digitalMax(digitalMax)
                .prefilterings(prefilterings).numberOfSamples(numberOfSamples)
                .reserveds(new byte[1][EDFConstants.RESERVED_SIZE]).build();
		return header;
	}
	
	/*
	 * Kirjoittaa EDF tiedoston annetusta tiedostopolusta, headerista (random signaaliarvot)
	 */
	public static void write(String path, EDFHeader header) {
		 
		 EDFSignal signal = new EDFSignal();
		 
		 // Asetetaan kanavien määrä
         signal.unitsInDigit = new Double[header.numberOfChannels];
         signal.digitalValues = new short[header.numberOfChannels][];
         signal.valuesInUnits = new double[header.numberOfChannels][];
         
         // Asetetaan yksikköarvot???
         for (int i = 0; i < signal.unitsInDigit.length; i++)
             signal.unitsInDigit[i] = (header.maxInUnits[i] - header.minInUnits[i])
                                      / (header.digitalMax[i] - header.digitalMin[i]);

         
         // Asetetaan mittauksien määrä kullekin kanavalle
         for (int i = 0; i < header.numberOfChannels; i++) {
        	 signal.digitalValues[i] = new short[header.numberOfRecords * header.numberOfSamples[i]];
             signal.valuesInUnits[i] = new double[header.numberOfRecords * header.numberOfSamples[i]];
         }
         
         // Todo!!! Tässä kohtaa pitäisi jotenkin sijoittaa NAN-arvot näihin:
         //		signal.digitalValues, 
         //		signal.valuesInUnits
         
         for (int i = 0; i < header.numberOfChannels; i++) {
        	 for (int j = 0; j < header.numberOfSamples[i]; j++) {
        		 signal.digitalValues[i][j] = (short) java.lang.Math.floor(30000*java.lang.Math.sin(j*0.1));
        		 signal.valuesInUnits[i][j] = (double) 1.0+java.lang.Math.sin(j*0.1);
        	 }
         }
         
         // Luodaan/tallennetaan EDF-tiedosto
         File file = new File(path);
 		 try {
 			OutputStream stream = new FileOutputStream(file);
 			EDFWriter.writeIntoOutputStream(header, stream);
 			EDFWriter.writeIntoOutputStream(signal, header, stream);
 			stream.close();
 			System.out.println("Success!");
 		 } catch (IOException e) {
 			e.printStackTrace();
 		 } 
	}
	

}
