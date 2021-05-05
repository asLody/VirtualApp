package com.lody.virtual.server.interfaces;

import android.app.job.JobInfo;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.List;

/**
 * @author Lody
 */
public interface IJobService {

    int schedule(JobInfo job) throws RemoteException;

    void cancel(int jobId) throws RemoteException;

    void cancelAll() throws RemoteException;

    List<JobInfo> getAllPendingJobs() throws RemoteException;

    int enqueue(JobInfo jobInfo, Parcelable parcelable) throws RemoteException;

    JobInfo getPendingJob(int i) throws RemoteException;
}
