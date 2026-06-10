package me.proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@SpringBootApplication
public class CalendarPlannerApplication implements WebMvcConfigurer {
  private final LocaleChangeInterceptor localeChangeInterceptor;

  public CalendarPlannerApplication(LocaleChangeInterceptor localeChangeInterceptor) {
    this.localeChangeInterceptor = localeChangeInterceptor;
  }

  public static void main(String[] args) {
    SpringApplication.run(
        CalendarPlannerApplication.class,
        args
    );
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor);
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("classpath:lang/messages");
    messageSource.setDefaultEncoding("windows-1251");
    return messageSource;
  }
}