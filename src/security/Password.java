package security;

import java.security.NoSuchAlgorithmException;

import com.apple.crypto.provider.MessageDigestMD5;

public class Password {
  private String md5string;

  public Password(String cleartext) {
    try {
      setMD5String(cleartext);
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Failed to encode password. Clearing password.\nDetails:\n" + e.getMessage());
    }
  }
  
  public void setMD5String(String string) throws NoSuchAlgorithmException {
    MessageDigestMD5 md5 = new MessageDigestMD5();
    md5.engineReset();
    md5.engineUpdate(string.getBytes(), 0, string.getBytes().length);
    this.md5string = md5.engineDigest().toString();
  }

  public String getMD5String() {
    return md5string;
  }
}
