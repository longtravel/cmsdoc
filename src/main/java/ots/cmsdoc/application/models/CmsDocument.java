package ots.cmsdoc.application.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "TSCNTRLT")
public class CmsDocument {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DOC_HANDLE", length = 30)
    //attempt to fix problem
    @GeneratedValue(generator="hibernate_sequence")
    @GenericGenerator(strategy="sequence", name="hibernate_sequence")
    private String id;

    @Type(type = "short")
    @Column(name = "DOC_SEGS")
    private Short segmentCount;

    @Type(type = "long")
    @Column(name = "DOC_LEN")
    private Long docLength;

    @Column(name = "DOC_AUTH")
    private String docAuth;

    @Column(name = "DOC_SERV")
    private String docServ;

    @Type(type = "date")
    @Column(name = "DOC_DATE")
    private Date docDate;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DomainChef.TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Type(type = "time")
    @Column(name = "DOC_TIME")
    private Date docTime;

    @Column(name = "DOC_NAME")
    private String docName;

    @Column(name = "CMPRS_PRG")
    private String compressionMethod;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, mappedBy = "docHandle")
    @OrderBy("DOC_HANDLE, DOC_SEGSEQ")
    private Set<CmsDocumentBlobSegment> blobSegments = new LinkedHashSet<>();

    public CmsDocument () {}

    /**
     * Convenience constructor.
     *
     * @param id unique document handle
     * @param segmentCount number of blob segments
     * @param docLength total bytes in all blob segments
     * @param docAuth document "auth"
     * @param docServ document "server"
     * @param docDate creation date
     * @param docTime creation time
     * @param docName document name (e.g., "word.doc")
     * @param compressionMethod type of compression, 01 (LZW) or 02 (PK)
     */
    public CmsDocument(String id, Short segmentCount, Long docLength, String docAuth, String docServ,
        Date docDate, Date docTime, String docName, String compressionMethod) {
        //super();
        this.id = id;
        this.docAuth = docAuth;
        this.docServ = docServ;
        this.docName = docName;
        this.segmentCount = segmentCount;
        //this.docDate = freshDate(docDate);
        this.docDate = docDate != null ? new Date(docDate.getTime()) : null;
        //this.docTime = freshDate(docTime);
        this.docTime = docTime != null ? new Date(docTime.getTime()) : null;
        this.docLength = docLength;
        this.compressionMethod = compressionMethod;
    }


    /**
     * {@inheritDoc}
     *
     */
    public Serializable getPrimaryKey() {
        return getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return segmentCount
     */
    public Short getSegmentCount() {
        return segmentCount;
    }

    /**
     * @param segmentCount segment count
     */
    public void setSegmentCount(Short segmentCount) {
        this.segmentCount = segmentCount;
    }

    /**
     * @return docLength
     */
    public Long getDocLength() {
        return docLength;
    }

    /**
     * @param docLength document length (uncompressed)
     */
    public void setDocLength(Long docLength) {
        this.docLength = docLength;
    }

    /**
     * @return docAuth doc "auth" stamp
     */
    public String getDocAuth() {
        return docAuth;
    }

    /**
     * @param docAuth doc "auth" stamp
     */
    public void setDocAuth(String docAuth) {
        this.docAuth = docAuth;
    }

    /**
     * @return docServ
     */
    public String getDocServ() {
        return docServ;
    }

    /**
     * @param docServ internal document server
     */
    public void setDocServ(String docServ) {
        this.docServ = docServ;
    }

    /**
     * @return docDate document date
     */
    public Date getDocDate() {
        //return freshDate(docDate);
        return docDate;
    }

    /**
     * @param docDate document date
     */
    public void setDocDate(Date docDate) {
        //this.docDate = freshDate(docDate);
        this.docDate = docDate != null ? new Date(docDate.getTime()) : null;
    }

    /**
     * @return docTime
     */
    public Date getDocTime() {
        return docTime;
    }

    /**
     * @param docTime time of document creation
     */
    public void setDocTime(Date docTime) {
        //this.docTime = freshDate(docTime);
        this.docTime = docTime != null ? new Date(docTime.getTime()) : null;
    }

    /**
     * @return docName document name, such as "child.doc"
     */
    public String getDocName() {
        return docName;
    }

    /**
     * @param docName document name, such as "child.doc"
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @return compressionMethod
     */
    public String getCompressionMethod() {
        return compressionMethod;
    }

    /**
     * @param compressionMethod compression method, PK or LZW
     */
    public void setCompressionMethod(String compressionMethod) {
        this.compressionMethod = compressionMethod;
    }

    /**
     * @param id char(10) identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return blobSegments Set of binary, compressed segments
     */
    public Set<CmsDocumentBlobSegment> getBlobSegments() {
        return blobSegments;
    }

    /**
     * @param blobSegments Set of binary, compressed segments
     */
    public void setBlobSegments(Set<CmsDocumentBlobSegment> blobSegments) {
        this.blobSegments = blobSegments;
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

}
