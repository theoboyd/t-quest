package security;

import java.util.Random;

import object.PlayerCharacter;

public class User {
  private static final int    BASIC_USER                 = 0;
  private static final int    PREMIUM_USER               = 1;
  private static final int    MOD_USER                   = 2;
  private static final int    ADMIN_USER                 = 3;
  private int                 privilegeLevel             = BASIC_USER;
  private static final String PASSWORD_DISPLAY_CHARACTER = "●";
  private String              cleartext;
  private Password            password;
  private String              username;
  public long                 loginUserID;
  public PlayerCharacter      playerCharacter;

  public int getPrivilegeLevel() {
    return privilegeLevel;
  }
  
  public User(String username, String cleartext) {
    Random r = new Random();
    int clearTextLength = cleartext.length();
    loginUserID = r.nextLong();
    this.username = username;
    this.password = new Password(cleartext); // Securely encode password.
    this.cleartext = "";
    for (int i = 0; i < clearTextLength; i++) {
      this.cleartext += PASSWORD_DISPLAY_CHARACTER;
    }
  }

  public boolean elevatePrivileges() {
    if (privilegeLevel == BASIC_USER) {
      return false;
    } else {
      if (privilegeLevel != ADMIN_USER) {
        privilegeLevel++;
      }
      return true;
    }
  }

  public boolean upgradeAccount() {
    if (privilegeLevel == BASIC_USER) {
      privilegeLevel = PREMIUM_USER;
      return true;
    } else {
      return false;
    }
  }

  public String getUsername() {
    return username;
  }

  public String getDisplayPassword() {
    return cleartext;
  }
  
  public String getHashedPassword() {
    return password.getMD5String();
  }
}
