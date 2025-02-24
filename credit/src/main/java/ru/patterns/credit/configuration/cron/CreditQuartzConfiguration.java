package ru.patterns.credit.configuration.cron;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import ru.patterns.credit.infrastructure.cron.AutoPaymentJob;

@Configuration
public class CreditQuartzConfiguration {

    @Bean
    public JobDetail paymentJobDetail() {
        return JobBuilder.newJob(AutoPaymentJob.class)
                .withIdentity("autoPaymentJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger paymentJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(paymentJobDetail())
                .withIdentity("autoPaymentTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            SpringBeanJobFactory jobFactory,
            JobDetail paymentJobDetail,
            Trigger paymentJobTrigger
    ) {
        var factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setJobDetails(paymentJobDetail);
        factoryBean.setTriggers(paymentJobTrigger);
        return factoryBean;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        var jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        var scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }
}
