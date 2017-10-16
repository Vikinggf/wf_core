package com.wf.core.idg.postgres;

import java.util.UUID;

import javax.transaction.xa.Xid;

/**
 * 基于uuid实现的xid
 * @author Fe
 * @version 2016年1月5日 <strong>1.0</strong>
 */
public class UUIDXid implements Xid {
	private final byte formatid = 100;
	private final byte[] branchid;
	private final byte[] globalid;
	private final UUID uuid;
	private UUIDXid() {
		uuid = UUID.randomUUID();
		long h = uuid.getMostSignificantBits();
		long l = uuid.getLeastSignificantBits();
		globalid = new byte[17];
		branchid = new byte[16];
		branchid[0] = (byte) (h >> 56 & 0XFF);
		branchid[1] = (byte) (h >> 48 & 0XFF);
		branchid[2] = (byte) (h >> 40 & 0XFF);
		branchid[3] = (byte) (h >> 32 & 0XFF);
		branchid[4] = (byte) (h >> 24 & 0XFF);
		branchid[5] = (byte) (h >> 16 & 0XFF);
		branchid[6] = (byte) (h >> 8 & 0XFF);
		branchid[7] = (byte) (h & 0XFF);
		branchid[8] = (byte) (l >> 56 & 0XFF);
		branchid[9] = (byte) (l >> 48 & 0XFF);
		branchid[10] = (byte) (l >> 40 & 0XFF);
		branchid[11] = (byte) (l >> 32 & 0XFF);
		branchid[12] = (byte) (l >> 24 & 0XFF);
		branchid[13] = (byte) (l >> 16 & 0XFF);
		branchid[14] = (byte) (l >> 8 & 0XFF);
		branchid[15] = (byte) (l & 0XFF);
		System.arraycopy(branchid, 0, globalid, 0, 16);
		globalid[16] = formatid;
	}
	
	public static UUIDXid getInstance() {
		return new UUIDXid();
	}

	@Override
	public byte[] getBranchQualifier() {
		return branchid;
	}
	@Override
	public int getFormatId() {
		return formatid;
	}
	@Override
	public byte[] getGlobalTransactionId() {
		return globalid;
	}

	@Override
	public String toString() {
		return "UUIDXid [" + uuid + "]";
	}
}