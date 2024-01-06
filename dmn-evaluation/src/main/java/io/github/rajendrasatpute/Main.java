package io.github.rajendrasatpute;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        evaluateDMNInResource();
        evaluateDMNAtPath("/home/user/dev/dmn-demo/dmn-evaluation/src/main/resources/dmn");
    }

    public static void evaluateDMNInResource() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kContainer.getKieBase()).get(DMNRuntime.class);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("AccountHolder", Map.of("YearlyIncome", 2000000, "IncomeSource", "SALARY"));
        dmnContext.set("Property", Map.of("PrivateValue", 8000000, "GovtValue", 7000000));
        dmnRuntime.getModels().forEach(dmnModel -> {
            System.out.println(dmnModel.getNamespace());
            System.out.println(dmnModel.getName());
            DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
            for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
                System.out.println("Decision: '" + dr.getDecisionName() + "', " + "Result: " + dr.getResult());
            }
        });
    }

    public static void evaluateDMNAtPath(String dirPath) {
        KieServices ks = KieServices.Factory.get();
        KieResources kieResources = ks.getResources();
        KieFileSystem kfs = ks.newKieFileSystem();
        try (Stream<Path> stream = Files.list(Paths.get(dirPath))) {
            stream.filter(file -> !Files.isDirectory(file))
                    .forEach(filePath -> kfs.write(kieResources.newFileSystemResource(filePath.toFile(), "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll();
        KieContainer kContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kContainer.getKieBase()).get(DMNRuntime.class);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("AccountHolder", Map.of("YearlyIncome", 2000000, "IncomeSource", "SALARY"));
        dmnContext.set("Property", Map.of("PrivateValue", 8000000, "GovtValue", 7000000));
        dmnRuntime.getModels().forEach(dmnModel -> {
            System.out.println(dmnModel.getNamespace());
            System.out.println(dmnModel.getName());
            DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
            for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
                System.out.println("Decision: '" + dr.getDecisionName() + "', " + "Result: " + dr.getResult());
            }
        });
    }
}
