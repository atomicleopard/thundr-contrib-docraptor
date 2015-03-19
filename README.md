thundr-contrib-docraptor [![Build Status](https://travis-ci.org/atomicleopard/thundr-contrib-docraptor.svg)](https://travis-ci.org/atomicleopard/thundr-contrib-docraptor)
=================

A thundr module for rendering documents using [Docraptor](http://docraptor.com/).

You can read more about thundr [here](http://3wks.github.io/thundr/)

Include the thundr-contrib-docraptor dependency using maven or your favourite dependency management tool.
    
    <dependency>
  		<groupId>com.atomicleopard</groupId>
		<artifactId>thundr-contrib-docraptor</artifactId>
		<version>1.0.0</version>
		<scope>compile</scope>
	</dependency>
    
Include your docraptor api key in ``application.properties``

    docraptorApiKey=YOUR_KEY_HERE

Add a dependency on the docraptor module in your ``ApplicationModule`` file:

	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		super.requires(dependencyRegistry);
		...
		dependencyRegistry.addDependency(DocraptorModule.class);
	}

The Docraptor interface will then be available for injection into services and controllers. Draw a PDF by
making a render call passing in a DocraptorRequest with the appropriate fields. 
    
    private Docraptor docraptor;
    ...
    DocraptorRequest request = DocraptorRequest.ForUrl("filename.pdf", absoluteUrl)
											   .withJavascript(true)
											   .withTestMode(testMode);
	byte[] pdfData = docraptor.render(request);
	
    
--------------    
thundr-contrib-docraptor - Copyright (C) 2015 Atomic Leopard    