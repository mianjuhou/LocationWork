package com.plutus.queue.querypool;

public class Request implements Comparable<Request> {

	private static int FIFO = Integer.MAX_VALUE-2;// 先进先出

	public static final int LIFO = 0;// 后进先出

	public Response response;

	private int sequenceNumber = 0;// 值越大越先执行

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * 默认是先进先出,当传入0代表先进后出,值越大，越先执行
	 * 
	 * @param sequenceNumber
	 * @return
	 */
	public Request setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		return this;
	}

	protected Object[] par;

	public Request(Response response, Object... obj) {
		this.response = response;
		this.par = obj;
		setSequenceNumber(FIFO--);
	}

	private Request() {
	}

	public Response getResponse() {
		return response;
	}

	public Object[] getParmter() {
		return par;
	}

	public int compareTo(Request another) {
		return another.getSequenceNumber() - sequenceNumber;
	}
}
