/*
 * Copyright (c) 2019, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License,
 * Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the
 * License.
 */

package com.okta.oidc.net.request;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import com.okta.oidc.net.ConnectionParameters;
import com.okta.oidc.net.params.TokenTypeHint;

import java.util.HashMap;
import java.util.Map;

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DeviceSecretTokenRequest extends TokenRequest {
    private static final String TAG = DeviceSecretTokenRequest.class.getSimpleName();

    DeviceSecretTokenRequest() {
    }

    DeviceSecretTokenRequest(HttpRequestBuilder.DeviceSecretTokenExchange b) {
        super();
        mRequestType = b.mRequestType;
        mConfig = b.mConfig;
        mProviderConfiguration = b.mProviderConfiguration;
        mUri = Uri.parse(mProviderConfiguration.token_endpoint);
        client_id = b.mConfig.getClientId();
        grant_type = b.mGrantType;
        mConnParams = new ConnectionParameters.ParameterBuilder()
                .setRequestMethod(ConnectionParameters.RequestMethod.POST)
                .setRequestProperty("Accept", ConnectionParameters.JSON_CONTENT_TYPE)
                .setPostParameters(buildParameters(b))
                .setRequestType(mRequestType)
                .create();
    }

    protected Map<String, String> buildParameters(HttpRequestBuilder.DeviceSecretTokenExchange b) {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", client_id);
        params.put("grant_type", grant_type);
        params.put("actor_token", b.mActorToken);
        params.put("actor_token_type", TokenTypeHint.ACTOR_TOKEN_TYPE);
        params.put("subject_token", b.mSubjectToken);
        params.put("subject_token_type", TokenTypeHint.SUBJECT_TOKEN_TYPE);
        params.put("scope", TextUtils.join(" ", b.mConfig.getScopes()).replace("device_sso", ""));
        params.put("audience", b.mConfig.getAudience());
        return params;
    }
}
