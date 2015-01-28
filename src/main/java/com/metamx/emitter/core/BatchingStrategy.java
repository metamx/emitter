package com.metamx.emitter.core;

public enum BatchingStrategy
{
  ARRAY {
    private final byte[] START = new byte[]{'['};
    private final byte[] SEPARATOR = new byte[]{','};
    private final byte[] END = new byte[]{']', '\n'};

    @Override
    public byte[] batchStart()
    {
      return START;
    }

    @Override
    public byte[] messageSeparator()
    {
      return SEPARATOR;
    }

    @Override
    public byte[] batchEnd()
    {
      return END;
    }
  },
  NEWLINES {
    private final byte[] START = new byte[0];
    private final byte[] SEPARATOR = new byte[]{'\n'};
    private final byte[] END = new byte[]{'\n'};

    @Override
    public byte[] batchStart()
    {
      return START;
    }

    @Override
    public byte[] messageSeparator()
    {
      return SEPARATOR;
    }

    @Override
    public byte[] batchEnd()
    {
      return END;
    }
  };

  public abstract byte[] batchStart();

  public abstract byte[] messageSeparator();

  public abstract byte[] batchEnd();
}
