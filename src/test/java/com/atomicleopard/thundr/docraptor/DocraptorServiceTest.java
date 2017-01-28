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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import com.atomicleopard.thundr.docraptor.DocraptorService.RequestWrapper;
import com.threewks.thundr.http.service.HttpResponse;
import com.threewks.thundr.http.service.test.MockHttpService;
import com.threewks.thundr.transformer.TransformerManager;
import com.threewks.thundr.view.ViewResolverRegistry;
import com.threewks.thundr.view.json.JsonView;
import com.threewks.thundr.view.string.StringView;
import com.threewks.thundr.view.string.StringViewResolver;

public class DocraptorServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MockHttpService httpService = new MockHttpService();
	private ViewResolverRegistry viewResolverRegistry = new ViewResolverRegistry();
	private String docraptorApiKey = "key";
	private DocraptorService docraptorService;

	@Before
	public void before() {
		viewResolverRegistry.addResolver(StringView.class, new StringViewResolver());
		docraptorService = new DocraptorService(httpService, viewResolverRegistry, TransformerManager.createWithDefaults(), docraptorApiKey);
	}

	@Test
	public void shouldPostToDocraptor() {
		byte[] data = new byte[] { 1, 2, 3 };
		HttpResponse expect = mock(HttpResponse.class);
		httpService.expected(expect);
		when(expect.getStatus()).thenReturn(200);
		when(expect.getBodyAsBytes()).thenReturn(data);

		assertThat(docraptorService.render(DocraptorRequest.ForUrl("name", "url")), is(data));
	}

	@Test
	public void shouldRenderViewAndPost() {
		byte[] data = new byte[] { 1, 2, 3 };

		HttpResponse expect = mock(HttpResponse.class);
		httpService.expected(expect);
		when(expect.getStatus()).thenReturn(200);
		when(expect.getBodyAsBytes()).thenReturn(data);

		docraptorService = spy(docraptorService);

		ArgumentCaptor<RequestWrapper> captor = ArgumentCaptor.forClass(RequestWrapper.class);
		doReturn(data).when(docraptorService).postContentToDocraptor(captor.capture());

		docraptorService.render(DocraptorRequest.ForContent("name", new StringView("OK")));

		RequestWrapper wrapper = captor.getValue();
		assertThat(wrapper.user_credentials, is("key"));
		assertThat(wrapper.doc.document_content, is("OK"));
	}

	@Test
	public void shouldThrowDocRaptorExceptionWhenPostFails() {
		thrown.expect(DocraptorException.class);

		HttpResponse expect = mock(HttpResponse.class);
		when(expect.getStatus()).thenReturn(422);

		httpService.expected(expect);

		docraptorService.render(DocraptorRequest.ForContent("name", new StringView("OK")));
	}

	@Test
	public void shouldThrowDocRaptorExceptionWhenViewRenderingFails() {
		thrown.expect(DocraptorException.class);

		docraptorService.render(DocraptorRequest.ForContent("name", new JsonView("OK")));
	}
}
