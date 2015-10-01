package net.whydah.sso.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UserToken implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(UserToken.class);
    private static String defcon;
    private String tokenid;
    //From UIB
    private String uid;
    private String personRef;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String cellPhone;
    private String timestamp;
    private String lastSeen;
    private String securityLevel;
    private String lifespan;
    private String issuer;
    private String ns2link;
    private List<ApplicationRoleEntry> roleList;
    private String applicationID = null;

    public UserToken() {
        this.timestamp = Long.toString(System.currentTimeMillis());
        this.lastSeen = new Date().toString();
        this.roleList = new LinkedList<>();
        this.defcon = DEFCON.DEFCON5.toString();
    }

    public static String getDefcon() {
        if (defcon == null || defcon.length() < 1) {
            return DEFCON.DEFCON5.toString();
        }
        return defcon;
    }

    public static void setDefcon(String defcon) {
        if (isInEnum(defcon, DEFCON.class)) {
            UserToken.defcon = defcon;

        } else {
            defcon = DEFCON.DEFCON5.toString();
        }
    }

    public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public boolean isValid() {
        if (timestamp == null || lifespan == null) {
            log.trace("usertoken invalid because timestamp or lifespan is not set. timestamp={}  lifespan={}", timestamp, lifespan);
            return false;
        }

        long now = System.currentTimeMillis();
        long timeout = Long.parseLong(timestamp) + Long.parseLong(lifespan);
        boolean stillValid = timeout > now;
        if (!stillValid) {
            log.trace("usertoken invalid (timed out). timeout={} is NOT greater than now={}", timeout, now);
        }
        log.trace("usertoken is valid!");
        return stillValid;
    }

    public UserToken copy() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte[] obj = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(obj);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (UserToken) ois.readObject();
        } catch (Exception e) {
            log.error("Error copying UserToken", e);
        }
        return null;
    }

    //Used by usertoken.ftl
    public String getMD5() {
        String md5base = null2empty(uid) + null2empty(personRef) + null2empty(tokenid) + null2empty(timestamp)
                + null2empty(firstName) + null2empty(lastName) + null2empty(email) + null2empty(cellPhone) + null2empty(securityLevel) + null2empty(issuer);
        System.out.println("MD5base: " + md5base);
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(md5base.getBytes("UTF-8"));
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(16);
        } catch (Exception e) {
            log.error("Error creating MD5 hash, returning empty string. userToken: " + toString(), e);
            return "";
        }
    }

    private String null2empty(String value) {
        return value != null ? value : "";
    }

    public void addApplicationRoleEntry(ApplicationRoleEntry role) {
        roleList.add(role);
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPersonRef() {
        return personRef;
    }

    public void setPersonRef(String personRef) {
        this.personRef = personRef;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getLifespan() {
        return lifespan;
    }

    public void setLifespan(String lifespan) {
        this.lifespan = lifespan;
    }

    // TODO  return a better issuer?
    public String getIssuer() {
        if (ns2link != null) {
            return ns2link;
        }
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public List<ApplicationRoleEntry> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<ApplicationRoleEntry> roleList) {
        this.roleList = roleList;
    }

    public String getNs2link() {
        return ns2link;
    }

    public void setNs2link(String ns2link) {
        this.ns2link = ns2link;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "tokenid='" + tokenid + '\'' +
                ", uid='" + uid + '\'' +
                ", personRef='" + personRef + '\'' +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", lastSeen='" + lastSeen + '\'' +
                ", securityLevel='" + securityLevel + '\'' +
                ", lifespan='" + lifespan + '\'' +
                ", issuer='" + issuer + '\'' +
                ", lastSeen='" + lastSeen + '\'' +
                ", roleList.size=" + getRoleList().size() +
                ", MD5='" + getMD5() + '\'' +
                '}';
    }

    public enum DEFCON {
        DEFCON1, DEFCON2, DEFCON3, DEFCON4, DEFCON5
    }

}