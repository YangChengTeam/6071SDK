package com.game.sdk;

import com.game.sdk.domain.RoleInfo;

public interface SetRoleListener {

	void roleSetSuccess(RoleInfo roleInfo);
	void roleSetFail();

}
