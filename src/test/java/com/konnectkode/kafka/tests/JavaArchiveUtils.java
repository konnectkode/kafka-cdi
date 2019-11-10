package com.konnectkode.kafka.tests;

import com.konnectkode.kafka.beans.KafkaMessageListener;
import com.konnectkode.kafka.cdi.extension.KafkaExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import com.konnectkode.kafka.cdi.annotation.KafkaConfig;
import com.konnectkode.kafka.config.KafkaConfigProperties;
import com.konnectkode.kafka.config.KafkaPropertiesResolverImpl;
import com.konnectkode.kafka.consumer.Delegation;

import javax.enterprise.inject.spi.Extension;

public interface JavaArchiveUtils {

    static JavaArchive createFrameworkDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(KafkaConfig.class.getPackage())
                .addClass(KafkaConfigProperties.class)
                .addClass(KafkaPropertiesResolverImpl.class)
                .addPackages(true, Delegation.class.getPackage())
                .addClasses(KafkaExtension.class)
                .addPackage(KafkaMessageListener.class.getPackage())
                .addAsServiceProvider(Extension.class, KafkaExtension.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    static JavaArchive createBasicConfigurationDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(KafkaConfig.class.getPackage())
                .addPackage(KafkaConfigProperties.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}