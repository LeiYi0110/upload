package com.bjxc.app.upload;

import com.bjxc.exception.BusinessException;

public class UploadException extends BusinessException {
	public UploadException(String msg) {
		super(msg);
	}
	
	public UploadException(String msg, Throwable cause) {
		super(msg, cause);
	}

	@Override
	public Integer getCode() {
		return 720;
	}

}
