/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2005. All Rights Reserved
 * 
 * DISCLAIMER: 
 * The following [enclosed] code is sample code created by IBM 
 * Corporation.  This sample code is not part of any standard IBM product 
 * and is provided to you solely for the purpose of assisting you in the 
 * development of your applications.  The code is provided 'AS IS', 
 * without warranty of any kind.  IBM shall not be liable for any damages 
 * arising out of your use of the sample code, even if they have been 
 * advised of the possibility of such damages.
 * =========================================================================
 */
package com.ibm.jzos.sample.fields;

import java.math.BigDecimal;

import com.ibm.jzos.fields.BinaryAsIntField;
import com.ibm.jzos.fields.BinaryAsLongField;
import com.ibm.jzos.fields.CobolDatatypeFactory;
import com.ibm.jzos.fields.ExternalDecimalAsBigDecimalField;
import com.ibm.jzos.fields.ExternalDecimalAsIntField;
import com.ibm.jzos.fields.Field;
import com.ibm.jzos.fields.PackedDecimalAsBigDecimalField;
import com.ibm.jzos.fields.PackedDecimalAsIntField;
import com.ibm.jzos.fields.StringField;



/**
 * A class which maps a fictional Medicare Record.
 * <p>
 * This class demonstrates how a COBOL copybook can be represented in Java using the
 * JZOS {@link Field} classes.  An instance of the {@link CobolDatatypeFactory} is
 * used to construct static member fields which represent the various fields from the
 * copybook.  The actual copybook field definitions are included as comments prior
 * to each Java field definition.
 * <p>
 * Instances of this class accept a byte array (either on the constructor or via the method
 * {@link #setBytes(byte[])}.  This byte array represents a COBOL record matching
 * the MedicareRecord described by the copybook.  Individual fields can be accessed via
 * the supplied getter and setter methods.
 * <p/>
 * @since 2.1.0
 */
public class MedicareRecord {
	
	public static final int BUF_LEN = 120;
	
	private byte[] bytes;

	private static CobolDatatypeFactory factory = new CobolDatatypeFactory();
	
	//	05 ClaimNumber                        PIC X(19).
	private static final StringField ClaimNumber = factory.getStringField(19);
	
	//	05 AdmissionDate       PACKED-DECIMAL PIC S9(7).
	private static final PackedDecimalAsIntField AdmissionDate  = factory.getPackedDecimalAsIntField(7, true);

	//	05 FromDate            PACKED-DECIMAL PIC S9(7).
	private static final PackedDecimalAsIntField FromDate = factory.getPackedDecimalAsIntField(7, true);

	//	05 ThruDate            PACKED-DECIMAL PIC S9(7).
	private static final PackedDecimalAsIntField ThruDate = factory.getPackedDecimalAsIntField(7, true);

	//	05 DischargeDate       PACKED-DECIMAL PIC S9(7).
	private static final PackedDecimalAsIntField DischargeDate = factory.getPackedDecimalAsIntField(7, true);

	//	05 FullDays            PACKED-DECIMAL PIC S9(5).
	private static final PackedDecimalAsIntField FullDays = factory.getPackedDecimalAsIntField(5, true);

	//	05 CoinsuranceDays     BINARY         PIC 9(4).
	private static final BinaryAsIntField CoinsuranceDays = factory.getBinaryAsIntField(4, false);
	
	//	05 LifetimeResDays     BINARY         PIC 9(6).
	private static final BinaryAsIntField LifetimeResDays = factory.getBinaryAsIntField(6, false);

	//	05 IntermediaryNum     BINARY         PIC 9(10).
	private static final BinaryAsLongField IntermediaryNum = factory.getBinaryAsLongField(10, false);

	//	05 MedicareProvider                   PIC X(13).
	private static final StringField MedicareProvider  = factory.getStringField(13, false);

	//	05 InpatientDed        PACKED-DECIMAL PIC S9(4)V99.
	private static final PackedDecimalAsBigDecimalField InpatientDed = factory.getPackedDecimalAsBigDecimalField(6, 2, true);
	
	//	05 BloodDed            PACKED-DECIMAL PIC S9(4)V99.
	private static final PackedDecimalAsBigDecimalField BloodDed = factory.getPackedDecimalAsBigDecimalField(6, 2, true);
	
	//	05 TotalCharges                       PIC S9(7)V99 DISPLAY SIGN LEADING.
	private static final ExternalDecimalAsBigDecimalField TotalCharges = factory.getExternalDecimalAsBigDecimalField(9, 2, true, false, false, false);
	
	//	05 PatientStatus                      PIC X(2).
	private static final StringField PatientStatus = factory.getStringField(2);

	//	05 BloodPintsFurnished BINARY         PIC 9(5).
	private static final BinaryAsIntField BloodPintsFurnished = factory.getBinaryAsIntField(5, false);

	//	05 BloodPintsReplaced  BINARY         PIC 9(4).
	private static final BinaryAsIntField BloodPintsReplaced = factory.getBinaryAsIntField(4, false);

	//	05 SequenceCounter     BINARY         PIC 9(3).
	private static final BinaryAsIntField SequenceCounter = factory.getBinaryAsIntField(3, false);

	//	05 TransactionInd                     PIC 9.
	private static final ExternalDecimalAsIntField TransactionInd = factory.getExternalDecimalAsIntField(1, false, false, false, false);

	//	05 BillSource                         PIC 9.
	private static final ExternalDecimalAsIntField BillSource = factory.getExternalDecimalAsIntField(1, false, false, false, false);

	//	05 BenefitsExhaustInd                 PIC 9.
	private static final ExternalDecimalAsIntField BenefitsExhaustInd = factory.getExternalDecimalAsIntField(1, false, false, false, false);

	//	05 BenefitsPayInd                     PIC 9.
	private static final ExternalDecimalAsIntField BenefitsPayInd = factory.getExternalDecimalAsIntField(1, false, false, false, false);

	//	05 AutoAdjustmentInd                  PIC X.
	private static final StringField AutoAdjustmentInd = factory.getStringField(1);

	//	05 IntermediaryCtrlNum                PIC X(23).
	private static final StringField IntermediaryCtrlNum = factory.getStringField(23);

	public MedicareRecord() {
		bytes = new byte[BUF_LEN];
	}
	
	public MedicareRecord(byte[] buffer) {
		setBytes(buffer);
	}

	/**
	 * Answer the underlying byte array mapped by this object.
	 * @return byte[]
	 */
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] buffer) {
		if (buffer.length < BUF_LEN) {
			throw new IllegalArgumentException("buffer length");
		}
		bytes = buffer;		
	}
	
	public int getAdmissionDate() {
		return AdmissionDate.getInt(bytes);
	}

	public void setAdmissionDate(int admissionDate) {
		AdmissionDate.putInt(admissionDate, bytes);
	}

	public String getAutoAdjustmentInd() {
		return AutoAdjustmentInd.getString(bytes);
	}

	public void setAutoAdjustmentInd(String autoAdjustmentInd) {
		AutoAdjustmentInd.putString(autoAdjustmentInd, bytes);
	}

	public int getBenefitsExhaustInd() {
		return BenefitsExhaustInd.getInt(bytes);
	}

	public void setBenefitsExhaustInd(int benefitsExhaustInd) {
		BenefitsExhaustInd.putInt(benefitsExhaustInd, bytes);
	}

	public int getBenefitsPayInd() {
		return BenefitsPayInd.getInt(bytes);
	}

	public void setBenefitsPayInd(int benefitsPayInd) {
		BenefitsPayInd.putInt(benefitsPayInd, bytes);
	}

	public int getBillSource() {
		return BillSource.getInt(bytes);
	}

	public void setBillSource(int billSource) {
		BillSource.putInt(billSource, bytes);
	}

	public BigDecimal getBloodDed() {
		return BloodDed.getBigDecimal(bytes);
	}

	public void setBloodDed(BigDecimal bloodDed) {
		BloodDed.putBigDecimal(bloodDed, bytes);
	}

	public int getBloodPintsFurnished() {
		return BloodPintsFurnished.getInt(bytes);
	}

	public void setBloodPintsFurnished(int bloodPintsFurnished) {
		BloodPintsFurnished.putInt(bloodPintsFurnished, bytes);
	}

	public int getBloodPintsReplaced() {
		return BloodPintsReplaced.getInt(bytes);
	}

	public void setBloodPintsReplaced(int bloodPintsReplaced) {
		BloodPintsReplaced.putInt(bloodPintsReplaced, bytes);
	}

	/**
	 * Get the ClaimNumber.
	 * <pre>
	 * 05 ClaimNumber                        PIC X(19).
	 * </pre>
	 * @return String the ClaimNumber
	 */
	public String getClaimNumber() {
		return ClaimNumber.getString(bytes);
	}

	/** 
	 * @see #getClaimNumber() 
	 */
	public void setClaimNumber(String claimNumber) {
		ClaimNumber.putString(claimNumber, bytes);
	}

	/**
	 * Get the CoinsuranceDays.
	 * <pre>
	 * 05 CoinsuranceDays     BINARY         PIC 9(4).
	 * </pre>
	 * @return int the CoinsuranceDays
	 */
	public int getCoinsuranceDays() {
		return CoinsuranceDays.getInt(bytes);
	}

	/**
	 * @see #getCoinsuranceDays()
	 */
	public void setCoinsuranceDays(int coinsuranceDays) {
		CoinsuranceDays.getInt(bytes);
	}

	/**
	 * Get the DischargeDate.
	 * <pre>
	 * 05 DischargeDate       PACKED-DECIMAL PIC S9(7).
	 * </pre>
	 * @return int the DischargeDate
	 */
	public int getDischargeDate() {
		return DischargeDate.getInt(bytes);
	}

	/**
	 * @see #getDischargeDate()
	 */
	public void setDischargeDate(int dischargeDate) {
		DischargeDate.putInt(dischargeDate, bytes);
	}

	/**
	 * Get the FromDate.
	 * <pre>
	 * 05 FromDate            PACKED-DECIMAL PIC S9(7).
	 * </pre>
	 * @return int the FromDate
	 */
	public int getFromDate() {
		return FromDate.getInt(bytes);
	}

	/**
	 * @see #getFromDate()
	 */
	public void setFromDate(int fromDate) {
		FromDate.putInt(fromDate, bytes);
	}

	/**
	 * Get the FullDays.
	 * <pre>
	 * 05 FullDays            PACKED-DECIMAL PIC S9(5).
	 * </pre>
	 * @return int the FullDays
	 */
	public int getFullDays() {
		return FullDays.getInt(bytes);
	}

	/**
	 * @see #getFullDays()
	 */
	public void setFullDays(int fullDays) {
		FullDays.putInt(fullDays, bytes);
	}

	/**
	 * Get the InpatientDed.
	 * <pre>
	 * 05 InpatientDed        PACKED-DECIMAL PIC S9(4)V99.
	 * </pre>
	 * @return BigDecimal the InpatientDed
	 */
	public BigDecimal getInpatientDed() {
		return InpatientDed.getBigDecimal(bytes);
	}

	/**
	 * @see #getInpatientDed()
	 */
	public void setInpatientDed(BigDecimal inpatientDed) {
		InpatientDed.putBigDecimal(inpatientDed, bytes);
	}

	/**
	 * Get the IntermediaryCtrlNum.
	 * <pre>
	 * 05 IntermediaryCtrlNum                PIC X(23).
	 * </pre>
	 * @return String the IntermediaryCtrlNum
	 */
	public String getIntermediaryCtrlNum() {
		return IntermediaryCtrlNum.getString(bytes);
	}

	/**
	 * @see #getIntermediaryCtrlNum()
	 */
	public void setIntermediaryCtrlNum(String intermediaryCtrlNum) {
		IntermediaryCtrlNum.putString(intermediaryCtrlNum, bytes);
	}

	/**
	 * Get the IntermediaryNum.
	 * <pre>
	 * 05 IntermediaryNum     BINARY         PIC 9(10).
	 * </pre>
	 * @return String the IntermediaryNum
	 */
	public long getIntermediaryNum() {
		return IntermediaryNum.getLong(bytes);
	}

	/**
	 * @see #getIntermediaryNum()
	 */
	public void setIntermediaryNum(long intermediaryNum) {
		IntermediaryNum.putLong(intermediaryNum, bytes);
	}

	/**
	 * Get the LifetimeResDays.
	 * <pre>
	 * 05 LifetimeResDays     BINARY         PIC 9(6).
	 * </pre>
	 * @return int the LifetimeResDays
	 */
	public int getLifetimeResDays() {
		return LifetimeResDays.getInt(bytes);
	}

	/**
	 * @see #getLifetimeResDays()
	 */
	public void setLifetimeResDays(int lifetimeResDays) {
		LifetimeResDays.getInt(bytes);
	}

	/**
	 * Get the MedicareProvider.
	 * <pre>
	 * 05 MedicareProvider                   PIC X(13).
	 * </pre>
	 * @return int the MedicareProvider
	 */
	public String getMedicareProvider() {
		return MedicareProvider.getString(bytes);
	}

	/**
	 * @see #getMedicareProvider()
	 */
	public void setMedicareProvider(String medicareProvider) {
		MedicareProvider.putString(medicareProvider, bytes);
	}

	/**
	 * Get the PatientStatus.
	 * <pre>
	 * 05 PatientStatus                      PIC X(2).
	 * </pre>
	 * @return String the PatientStatus
	 */
	public String getPatientStatus() {
		return PatientStatus.getString(bytes);
	}

	/**
	 * @see #getPatientStatus()
	 */
	public void setPatientStatus(String patientStatus) {
		PatientStatus.putString(patientStatus, bytes);
	}

	/**
	 * Get the SequenceCounter.
	 * <pre>
	 * 05 SequenceCounter     BINARY         PIC 9(3).
	 * </pre>
	 * @return int the SequenceCounter
	 */
	public int getSequenceCounter() {
		return SequenceCounter.getInt(bytes);
	}

	/**
	 * @see #getSequenceCounter()
	 */
	public void setSequenceCounter(int sequenceCounter) {
		SequenceCounter.putInt(sequenceCounter, bytes);
	}

	/**
	 * Get the ThruDate.
	 * <pre>
	 * 05 ThruDate            PACKED-DECIMAL PIC S9(7).
	 * </pre>
	 * @return int the ThruDate
	 */
	public int getThruDate() {
		return ThruDate.getInt(bytes);
	}

	/**
	 * @see #getThruDate()
	 */
	public void setThruDate(int thruDate) {
		ThruDate.putInt(thruDate, bytes);
	}

	/**
	 * Get the TotalCharges.
	 * <pre>
	 * 05 TotalCharges                       PIC S9(7)V99 DISPLAY SIGN LEADING.
	 * </pre>
	 * @return BigDecimal the TotalCharges
	 */
	public BigDecimal getTotalCharges() {
		return TotalCharges.getBigDecimal(bytes);
	}

	/**
	 * @see #getTotalCharges()
	 */
	public void setTotalCharges(BigDecimal totalCharges) {
		TotalCharges.putBigDecimal(totalCharges, bytes);
	}

	/**
	 * Get the TransactionInd.
	 * <pre>
	 * 05 TransactionInd                     PIC 9.
	 * </pre>
	 * @return int the TransactionInd
	 */
	public int getTransactionInd() {
		return TransactionInd.getInt(bytes);
	}

	/**
	 * @see #getTransactionInd()
	 */
	public void setTransactionInd(int transactionInd) {
		TransactionInd.putInt(transactionInd, bytes);
	}
	
}
