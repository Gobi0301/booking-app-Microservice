package com.booking.ms.type;

public enum BookingStatus {

	BOOKED("BOOKED"),NOTBOOKED("NOT BOOKED");
	
	public final String status;
	
	private BookingStatus(String status) {
		this.status = status;
	}
}
