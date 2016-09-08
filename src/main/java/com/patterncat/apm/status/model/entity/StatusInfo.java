package com.patterncat.apm.status.model.entity;

import com.patterncat.apm.status.model.BaseEntity;
import com.patterncat.apm.status.model.IVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatusInfo extends BaseEntity<StatusInfo> {
    private java.util.Date m_timestamp;

    private RuntimeInfo m_runtime;

    private OsInfo m_os;

    private DiskInfo m_disk;

    private MemoryInfo m_memory;

    private ThreadsInfo m_thread;

    private MessageInfo m_message;

    private Map<String, Extension> m_extensions = new LinkedHashMap<String, Extension>();

    public StatusInfo() {
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visitStatus(this);
    }

    public StatusInfo addExtension(Extension extension) {
        m_extensions.put(extension.getId(), extension);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatusInfo) {
            StatusInfo _o = (StatusInfo) obj;

            if (!equals(m_timestamp, _o.getTimestamp())) {
                return false;
            }

            if (!equals(m_runtime, _o.getRuntime())) {
                return false;
            }

            if (!equals(m_os, _o.getOs())) {
                return false;
            }

            if (!equals(m_disk, _o.getDisk())) {
                return false;
            }

            if (!equals(m_memory, _o.getMemory())) {
                return false;
            }

            if (!equals(m_thread, _o.getThread())) {
                return false;
            }

            if (!equals(m_message, _o.getMessage())) {
                return false;
            }

            if (!equals(m_extensions, _o.getExtensions())) {
                return false;
            }


            return true;
        }

        return false;
    }

    public Extension findExtension(String id) {
        return m_extensions.get(id);
    }

    public Extension findOrCreateExtension(String id) {
        Extension extension = m_extensions.get(id);

        if (extension == null) {
            synchronized (m_extensions) {
                extension = m_extensions.get(id);

                if (extension == null) {
                    extension = new Extension(id);
                    m_extensions.put(id, extension);
                }
            }
        }

        return extension;
    }

    public DiskInfo getDisk() {
        return m_disk;
    }

    public Map<String, Extension> getExtensions() {
        return m_extensions;
    }

    public MemoryInfo getMemory() {
        return m_memory;
    }

    public MessageInfo getMessage() {
        return m_message;
    }

    public OsInfo getOs() {
        return m_os;
    }

    public RuntimeInfo getRuntime() {
        return m_runtime;
    }

    public ThreadsInfo getThread() {
        return m_thread;
    }

    public java.util.Date getTimestamp() {
        return m_timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + (m_timestamp == null ? 0 : m_timestamp.hashCode());
        hash = hash * 31 + (m_runtime == null ? 0 : m_runtime.hashCode());
        hash = hash * 31 + (m_os == null ? 0 : m_os.hashCode());
        hash = hash * 31 + (m_disk == null ? 0 : m_disk.hashCode());
        hash = hash * 31 + (m_memory == null ? 0 : m_memory.hashCode());
        hash = hash * 31 + (m_thread == null ? 0 : m_thread.hashCode());
        hash = hash * 31 + (m_message == null ? 0 : m_message.hashCode());
        hash = hash * 31 + (m_extensions == null ? 0 : m_extensions.hashCode());

        return hash;
    }

    @Override
    public void mergeAttributes(StatusInfo other) {
        if (other.getTimestamp() != null) {
            m_timestamp = other.getTimestamp();
        }
    }

    public Extension removeExtension(String id) {
        return m_extensions.remove(id);
    }

    public StatusInfo setDisk(DiskInfo disk) {
        m_disk = disk;
        return this;
    }

    public StatusInfo setMemory(MemoryInfo memory) {
        m_memory = memory;
        return this;
    }

    public StatusInfo setMessage(MessageInfo message) {
        m_message = message;
        return this;
    }

    public StatusInfo setOs(OsInfo os) {
        m_os = os;
        return this;
    }

    public StatusInfo setRuntime(RuntimeInfo runtime) {
        m_runtime = runtime;
        return this;
    }

    public StatusInfo setThread(ThreadsInfo thread) {
        m_thread = thread;
        return this;
    }

    public StatusInfo setTimestamp(java.util.Date timestamp) {
        m_timestamp = timestamp;
        return this;
    }

}
