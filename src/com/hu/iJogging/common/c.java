package com.hu.iJogging.common;

public class c {

  private static char[] a = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
  private static byte[] b = new byte[256];

  public static char[] a(char[] paramArrayOfChar)
  {
    char[] arrayOfChar = new char[(paramArrayOfChar.length + 2) / 3 * 4];
    int i = 0;
    for (int j = 0; i < paramArrayOfChar.length; j += 4)
    {
      int k = 0;
      int m = 0;
      int n = 0xFF & paramArrayOfChar[i];
      n <<= 8;
      if (i + 1 < paramArrayOfChar.length)
      {
        n |= 0xFF & paramArrayOfChar[(i + 1)];
        m = 1;
      }
      n <<= 8;
      if (i + 2 < paramArrayOfChar.length)
      {
        n |= 0xFF & paramArrayOfChar[(i + 2)];
        k = 1;
      }
      arrayOfChar[(j + 3)] = a[64];
      n >>= 6;
      arrayOfChar[(j + 2)] = a[64];
      n >>= 6;
      arrayOfChar[(j + 1)] = a[(n & 0x3F)];
      n >>= 6;
      arrayOfChar[(j + 0)] = a[(n & 0x3F)];
      i += 3;
    }
    return arrayOfChar;
  }

  public static char[] b(char[] paramArrayOfChar)
  {
    int i = (paramArrayOfChar.length + 3) / 4 * 3;
    if ((paramArrayOfChar.length > 0) && (paramArrayOfChar[(paramArrayOfChar.length - 1)] == '='))
      i--;
    if ((paramArrayOfChar.length > 1) && (paramArrayOfChar[(paramArrayOfChar.length - 2)] == '='))
      i--;
    char[] arrayOfChar = new char[i];
    int j = 0;
    int k = 0;
    int m = 0;
    for (int n = 0; n < paramArrayOfChar.length; n++)
    {
      int i1 = b[(paramArrayOfChar[n] & 0xFF)];
      if (i1 >= 0)
      {
        k <<= 6;
        j += 6;
        k |= i1;
        if (j >= 8)
        {
          j -= 8;
          arrayOfChar[(m++)] = ((char)(k >> j & 0xFF));
        }
      }
    }
    if (m != arrayOfChar.length)
      throw new Error("miscalculated data length!");
    return arrayOfChar;
  }

  static
  {
    for (int i = 0; i < 256; i++)
      b[i] = -1;
    for (int i = 65; i <= 90; i++)
      b[i] = ((byte)(i - 65));
    for (int i = 97; i <= 122; i++)
      b[i] = ((byte)(26 + i - 97));
    for (int i = 48; i <= 57; i++)
      b[i] = ((byte)(52 + i - 48));
    b[43] = 62;
    b[47] = 63;
  }

}
