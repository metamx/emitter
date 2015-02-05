/*
 * Copyright 2012 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
