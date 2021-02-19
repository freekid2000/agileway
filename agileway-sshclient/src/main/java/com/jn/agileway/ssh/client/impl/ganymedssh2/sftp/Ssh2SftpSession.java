package com.jn.agileway.ssh.client.impl.ganymedssh2.sftp;

import ch.ethz.ssh2.*;
import com.jn.agileway.ssh.client.sftp.*;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class Ssh2SftpSession extends AbstractSftpSession {
    private SFTPv3Client sftpClient;

    public Ssh2SftpSession(SFTPv3Client sftpClient) {
        this.sftpClient = sftpClient;
    }

    public SFTPv3Client getSftpClient() {
        return sftpClient;
    }

    @Override
    protected List<SftpResourceInfo> doListFiles(String directory) throws IOException {
        Vector<SFTPv3DirectoryEntry> vector = sftpClient.ls(directory);
        return Pipeline.of(vector).filter(new Predicate<SFTPv3DirectoryEntry>() {
            @Override
            public boolean test(SFTPv3DirectoryEntry entry) {
                return !".".equals(entry.filename) && !"..".equals(entry.filename);
            }
        }).map(new Function<SFTPv3DirectoryEntry, SftpResourceInfo>() {
            @Override
            public SftpResourceInfo apply(SFTPv3DirectoryEntry entry) {
                FileAttrs attrs = Ssh2Sftps.fromSsh2FileAttributes(entry.attributes);
                return new SftpResourceInfo(entry.longEntry, attrs);
            }
        }).asList();

    }

    @Override
    public int getProtocolVersion() throws SftpException {
        return sftpClient.getProtocolVersion();
    }

    @Override
    public SftpFile open(String filepath, int openMode, FileAttrs attrs) throws IOException {
        if (!Sftps.exist(this, filepath)) {
            if (OpenMode.isCreatable(openMode)) {
                sftpClient.createFileTruncate(filepath, Ssh2Sftps.toSsh2FileAttributes(attrs));
            } else {
                throw new NoSuchFileSftpException(StringTemplates.formatWithPlaceholder("no such file: {}", filepath));
            }
        } else {
            if (OpenMode.isTruncated(openMode)) {
                sftpClient.createFileTruncate(filepath, Ssh2Sftps.toSsh2FileAttributes(attrs));
            }
        }

        SFTPv3FileHandle handle = null;
        if (OpenMode.isWritable(openMode)) {
            handle = sftpClient.openFileRW(filepath);
        } else {
            handle = sftpClient.openFileRO(filepath);
        }

        Ssh2SftpFile sftpFile = new Ssh2SftpFile(this, filepath);
        sftpFile.setFileHandle(handle);
        return sftpFile;
    }

    @Override
    public void createSymlink(String src, String target) throws IOException {
        try {
            sftpClient.createSymlink(src, target);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public String readLink(String path) throws IOException {
        try {
            return sftpClient.readLink(path);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        try {
            return sftpClient.canonicalPath(path);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs stat(String filepath) throws IOException {
        try {
            SFTPv3FileAttributes attributes = sftpClient.stat(filepath);
            return Ssh2Sftps.fromSsh2FileAttributes(attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public FileAttrs lstat(String filepath) throws IOException {
        try {
            SFTPv3FileAttributes attributes = sftpClient.lstat(filepath);
            return Ssh2Sftps.fromSsh2FileAttributes(attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws IOException {
        try {
            SFTPv3FileAttributes attributes = Ssh2Sftps.toSsh2FileAttributes(attrs);
            sftpClient.setstat(path, attributes);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mkdir(String directory, FileAttrs attributes) throws IOException {
        try {
            FileMode fileMode = attributes == null ? null : attributes.getFileMode();
            int permissions = fileMode == null ? 0 : fileMode.getMask();
            sftpClient.mkdir(directory, permissions);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rmdir(String directory) throws IOException {
        try {
            sftpClient.rmdir(directory);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void rm(String filepath) throws IOException {
        try {
            sftpClient.rm(filepath);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws IOException {
        try {
            sftpClient.mv(oldFilepath, newFilepath);
        } catch (SFTPException ex) {
            throw Ssh2Sftps.wrapSftpException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        sftpClient.close();
    }
}
