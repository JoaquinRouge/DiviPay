package com.divipay.group.configuration;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

public class FeignClientConfiguration implements RequestInterceptor{

	@Override
	public void apply(RequestTemplate template) {
		
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String userId = request.getHeader("X-User-Id");
                String email = request.getHeader("X-Email");
                String hasPaid = request.getHeader("X-Has-Paid");
                String signature = request.getHeader("X-Signature");

                if (userId != null) template.header("X-User-Id", userId);
                if (email != null) template.header("X-Email", email);
                if (hasPaid != null) template.header("X-Has-Paid", hasPaid);
                if (signature != null) template.header("X-Signature", signature);
                
            }
	}

}
