package com.lody.virtual.helper.proto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Lody
 *
 */
public class InstallResult implements Parcelable {

	public static final Creator<InstallResult> CREATOR = new Creator<InstallResult>() {
		public InstallResult createFromParcel(Parcel source) {
			return new InstallResult(source);
		}

		public InstallResult[] newArray(int size) {
			return new InstallResult[size];
		}
	};
	public boolean isSuccess;
	public boolean isUpdate;
	public String packageName;
	public String error;

	public InstallResult() {
	}

	public static InstallResult makeFailure(String error) {
		InstallResult res = new InstallResult();
		res.error = error;
		return res;
	}

	protected InstallResult(Parcel in) {
		this.isSuccess = in.readByte() != 0;
		this.packageName = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(isSuccess ? (byte) 1 : (byte) 0);
		dest.writeString(this.packageName);
	}
}
