/*
 * This file is a part of thundr-contrib-docraptor, a software library from Atomic Leopard.
 *
 * Copyright (C) 2015 Atomic Leopard, <nick@atomicleopard.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomicleopard.thundr.docraptor;

import com.google.gson.Gson;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.http.service.HttpResponse;
import com.threewks.thundr.http.service.HttpService;
import com.threewks.thundr.json.GsonSupport;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.view.BasicViewRenderer;
import com.threewks.thundr.view.ViewResolverRegistry;

public class DocraptorService implements Docraptor {
	protected String docRaptorUrl = "http://docraptor.com/docs";
	protected HttpService httpService;
	protected ViewResolverRegistry viewResolverRegistry;
	protected Gson gson = GsonSupport.createBasicGsonBuilder().create();
	protected String apiKey;

	public DocraptorService(HttpService httpService, ViewResolverRegistry viewResolverRegistry, String docraptorApiKey) {
		this.httpService = httpService;
		this.apiKey = docraptorApiKey;
		this.viewResolverRegistry = viewResolverRegistry;
	}

	@Override
	public byte[] render(DocraptorRequest request) throws DocraptorException {
		if (request.shouldRenderView()) {
			request = renderView(request);
		}
		RequestWrapper wrapper = new RequestWrapper(request);
		return postContentToDocraptor(wrapper);
	}

	protected byte[] postContentToDocraptor(RequestWrapper wrapper) {
		Logger.debug("Posting %s to %s %s", wrapper.doc.name, docRaptorUrl, wrapper.doc.test ? "(test mode)" : "");
		// @formatter:off
		String json = gson.toJson(wrapper);
		HttpResponse resp = httpService.request(docRaptorUrl)
								.contentType(ContentType.ApplicationJson.value())
								.body(json)
								.post();
		//@formatter:on
		int status = resp.getStatus();
		if (status != 200) {
			throw new DocraptorException("Failed to create document %s using docraptor - docraptor return status code %s", wrapper.doc.name, status);
		}
		return resp.getBodyAsBytes();
	}

	protected DocraptorRequest renderView(DocraptorRequest request) {
		try {
			BasicViewRenderer renderer = new BasicViewRenderer(viewResolverRegistry);
			renderer.render(request.contentView);
			String output = renderer.getOutputAsString();
			return request.withDocumentContent(output);
		} catch (RuntimeException e) {
			throw new DocraptorException(e, "Failed to render content for docraptor request: %s", e.getMessage());
		}
	}

	class RequestWrapper {
		public String user_credentials = apiKey;
		public DocraptorRequest doc;

		public RequestWrapper(DocraptorRequest request) {
			this.doc = request;
		}
	}

}
