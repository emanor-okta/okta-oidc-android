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

package com.okta.oidc.net.request.web;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.google.gson.Gson;
import com.okta.oidc.OIDCConfig;
import com.okta.oidc.net.request.ProviderConfiguration;
import com.okta.oidc.net.response.TokenResponse;
import com.okta.oidc.util.AuthorizationException;
import com.okta.oidc.util.CodeVerifierUtil;

/**
 * @hide
 */
//https://developer.okta.com/docs/api/resources/oidc#logout
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LogoutRequest extends WebRequest {
    private Parameters mParameters;

    LogoutRequest(Parameters parameters) {
        mParameters = parameters;
    }

    @Override
    public String getState() {
        if (mParameters != null) {
            return mParameters.state;
        }
        return null;
    }

    @Override
    @NonNull
    public Uri toUri() {
        return mParameters.toUri();
    }

    @NonNull
    public String getKey() {
        return RESTORE.getKey();
    }

    @Override
    public String persist() {
        return new Gson().toJson(mParameters);
    }

    static class Parameters {
        Parameters() {
            //NO-OP
        }

        String end_session_endpoint; //required
        String id_token_hint; //required
        String post_logout_redirect_uri; //required
        String state;
        String device_secret;

        Uri toUri() {
            Uri.Builder uriBuilder = Uri.parse(end_session_endpoint).buildUpon()
                    .appendQueryParameter("id_token_hint", id_token_hint);
            appendOptionalParams(uriBuilder, "post_logout_redirect_uri",
                    post_logout_redirect_uri);
            appendOptionalParams(uriBuilder, "state", state);
            appendOptionalParams(uriBuilder, "device_secret", device_secret);
            return uriBuilder.build();
        }

        private void appendOptionalParams(Uri.Builder builder, String name, String value) {
            if (value != null) {
                builder.appendQueryParameter(name, value);
            }
        }
    }

    public static final class Builder {
        private Parameters mParameters;

        private void validate() throws AuthorizationException {
            if (TextUtils.isEmpty(mParameters.end_session_endpoint)) {
                throw new AuthorizationException("end_session_endpoint missing",
                        new RuntimeException());
            }
            if (TextUtils.isEmpty(mParameters.id_token_hint)) {
                throw new AuthorizationException("id_token_hint missing", new RuntimeException());
            }
            if (TextUtils.isEmpty(mParameters.post_logout_redirect_uri)) {
                throw new AuthorizationException("post_logout_redirect_uri missing",
                        new RuntimeException());
            }

        }

        public Builder() {
            mParameters = new Parameters();
            mParameters.state = CodeVerifierUtil.generateRandomState();
        }

        public LogoutRequest create() throws AuthorizationException {
            validate();
            return new LogoutRequest(mParameters);
        }

        public Builder idTokenHint(@NonNull String idToken) {
            mParameters.id_token_hint = idToken;
            return this;
        }

        public Builder endSessionEndpoint(@NonNull String endpoint) {
            mParameters.end_session_endpoint = endpoint;
            return this;
        }

        public Builder postLogoutRedirect(@NonNull String redirectUri) {
            mParameters.post_logout_redirect_uri = redirectUri;
            return this;
        }

        public Builder state(@Nullable String state) {
            mParameters.state = state;
            return this;
        }

        public Builder deviceSecret(@Nullable String deviceSecret) {
            mParameters.device_secret = deviceSecret;
            return this;
        }

        public Builder provideConfiguration(@NonNull ProviderConfiguration providerConfiguration) {
            mParameters.end_session_endpoint = providerConfiguration.end_session_endpoint;
            return this;
        }

        public Builder tokenResponse(@NonNull TokenResponse tokenResponse) {
            mParameters.id_token_hint = tokenResponse.getIdToken();
            mParameters.device_secret = tokenResponse.getDeviceSecret();
            return this;
        }

        public Builder config(OIDCConfig config) {
            mParameters.post_logout_redirect_uri = config.getEndSessionRedirectUri().toString();
            return this;
        }
    }
}
