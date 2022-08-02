package com.android.split.logic;

import java.util.HashMap;
import java.util.Map;

public class Logic {

	private static Logic logic;

	private int numPeople;
	private final Map<String, Integer> nameToIndex;
	/*
     * Rows represent senders
     * Columns represent receivers
     * 
     * Last row at i-th column is the total amount of money the i-th receiver must receive
     * Last column at i-th row is the total amount of money the i-th sender must send
	 */
	private int[][] transferTable;

	private Logic() {
		this.numPeople = 0;
		this.nameToIndex = new HashMap<>();
	}

	public void addPeople(String[] names) {
		this.numPeople = names.length;

		// Add people and their index
		for (int i = 0; i < this.numPeople; i++) {
			this.nameToIndex.put(names[i], i);
		}
		this.nameToIndex.put("A", Integer.MAX_VALUE);
		
		// Create transferTable
		this.transferTable = new int[this.numPeople + 1][this.numPeople + 1];
	}

	public void addTransfer(String sender, String rcver, int amount, boolean replace)
			throws IllegalArgumentException {
		// If #addPeople(String[]) method was not called
		if (this.transferTable == null) {
			throw new RuntimeException("Enter names first");
		}

		int senderIndex = this.getNameIndex(sender);
		int rcverIndex = this.getNameIndex(rcver);
		if (senderIndex == -1 || rcverIndex == -1 || (senderIndex == Integer.MAX_VALUE && rcverIndex == Integer.MAX_VALUE)) {
			throw new IllegalArgumentException(String.format("Invalid name: %s, %s, %d", sender, rcver, amount));
		}

		if (senderIndex == Integer.MAX_VALUE) {
			for (int i = 0; i < this.numPeople; i++) {
				if (i != rcverIndex) {
					this.setAmount(i, rcverIndex, amount, replace);
				}
			}
			return;
		}

		if (rcverIndex == Integer.MAX_VALUE) {
			for (int i = 0; i < this.numPeople; i++) {
				if (i != senderIndex) {
					this.setAmount(senderIndex, i, amount, replace);
				}
			}
			return;
		}

		this.setAmount(senderIndex, rcverIndex, amount, replace);
	}

	public void simplify() {
		int[] numTransfers = new int[1];
		boolean hasSimplified;
		do {
			hasSimplified = false;
			for (int i = 0; i < this.numPeople; i++) {
				numTransfers[0] = this.countTransfers(i);
				for (int j = 0; j < this.numPeople; j++) {
					if (this.transferTable[i][j] == 0) {
						continue;
					}
					if (numTransfers[0] <= 0) {
						break;
					}

					// this.transferTable[i][j] != 0 && numTransfers[0] > 0
					if (this.transferTable[j][i] != 0) {
						// If two people both have money to send to each other, then cancel out
						this.simplify(i, j, numTransfers);
						hasSimplified = true;
					} else {
						for (int k = 0; k < this.numPeople; k++) {
							if (this.transferTable[i][k] != 0 && this.transferTable[k][j] != 0) {
								this.simplify(i, j, k, numTransfers);
							}
							if (numTransfers[0] <= 0) {
								break;
							}
						}
					}
				}
			}
		} while (hasSimplified);

		int totalSend, totalRcv;
		for (int i = 0; i < this.numPeople; i++) {
			totalSend = 0;
			totalRcv = 0;
			for (int j = 0; j < this.numPeople; j++) {
				totalSend += this.transferTable[i][j];
				totalRcv += this.transferTable[j][i];
			}
			this.transferTable[i][this.numPeople] = totalSend;
			this.transferTable[this.numPeople][i] = totalRcv;
		}
	}

	private void simplify(int senderIndex, int rcverIndex, int[] numTransfers) {
		int maxSenderIndex = senderIndex, maxRcverIndex = rcverIndex;
		if (this.transferTable[senderIndex][rcverIndex] < this.transferTable[rcverIndex][senderIndex]) {
			maxSenderIndex = rcverIndex;
			maxRcverIndex = senderIndex;
		}
		// Cancel out the transfers
		this.transferTable[maxSenderIndex][maxRcverIndex] -= this.transferTable[maxRcverIndex][maxSenderIndex];
		this.transferTable[maxRcverIndex][maxSenderIndex] = 0;
		// Update numTransfers
		numTransfers[0] = this.transferTable[senderIndex][rcverIndex] == 0 ? numTransfers[0] - 1 : numTransfers[0];
	}

	private void simplify(int senderIndex, int rcverIndex, int auxilliary, int[] numTransfers) {
		// Cancel out the transfers
		if (this.transferTable[auxilliary][rcverIndex] > this.transferTable[senderIndex][auxilliary]) {
			this.transferTable[senderIndex][rcverIndex] += this.transferTable[senderIndex][auxilliary];
			this.transferTable[auxilliary][rcverIndex] -= this.transferTable[senderIndex][auxilliary];
			this.transferTable[senderIndex][auxilliary] = 0;
		} else {
			this.transferTable[senderIndex][auxilliary] += this.transferTable[senderIndex][rcverIndex];
			this.transferTable[auxilliary][rcverIndex] += this.transferTable[senderIndex][rcverIndex];
			this.transferTable[senderIndex][rcverIndex] = 0;
		}
		// Update numTransfers
		numTransfers[0] = this.transferTable[senderIndex][rcverIndex] == 0 ? numTransfers[0] - 1 : numTransfers[0];
		numTransfers[0] = this.transferTable[senderIndex][auxilliary] == 0 ? numTransfers[0] - 1 : numTransfers[0];
	}

	// For testing purposes
	public void printTable() {
		int longest = Integer.MIN_VALUE;
		int length;
		for (int[] transfers : this.transferTable) {
			for (int transfer : transfers) {
				length = String.valueOf(transfer).length();
				longest = Math.max(longest, length + 1);
			}
		}

		for (int[] transfers : this.transferTable) {
			for (int transfer : transfers) {
				System.out.printf("%" + longest + "s", transfer);
			}
			System.out.println();
		}
	}

	private int getNameIndex(String name) {
		Integer integer = this.nameToIndex.get(name);
		return integer != null ? integer : -1;
	}

	private void setAmount(int senderIndex, int rcverIndex, int amount, boolean replace) {
		int amountToSet = this.transferTable[senderIndex][rcverIndex];
		this.transferTable[senderIndex][rcverIndex] = replace ? amount : amount + amountToSet;
		// Update the total amount of money to send/rcv
		int totalAmountSend = this.transferTable[senderIndex][this.numPeople];
		this.transferTable[senderIndex][this.numPeople] = replace
				? totalAmountSend + (amount - amountToSet)
				: totalAmountSend + amount;
		int totalAmountRcv = this.transferTable[this.numPeople][rcverIndex];
		this.transferTable[this.numPeople][rcverIndex] = replace
				? totalAmountRcv + (amount - amountToSet)
				: totalAmountRcv + amount;
	}

	private int countTransfers(int senderIndex) {
		int numTransfers = 0;
		for (int transfer : this.transferTable[senderIndex]) {
			numTransfers = transfer == 0 ? numTransfers : numTransfers + 1;
		}
		return numTransfers;
	}

	public static Logic create() {
		if (logic == null) {
			return new Logic();
		}
		return logic;
	}

}