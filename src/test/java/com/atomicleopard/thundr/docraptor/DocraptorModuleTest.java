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

import org.junit.Test;

import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.view.ViewModule;

public class DocraptorModuleTest {

	@Test
	public void shouldDependOnViewModule() {
		DependencyRegistry dependencyRegistry = new DependencyRegistry();
		new DocraptorModule().requires(dependencyRegistry);
		assertThat(dependencyRegistry.hasDependency(ViewModule.class), is(true));
	}

	@Test
	public void shouldInjectDocraptorService() {
		UpdatableInjectionContext injectionContext = new InjectionContextImpl();
		new DocraptorModule().configure(injectionContext);

		assertThat(injectionContext.contains(Docraptor.class), is(true));

	}
}
