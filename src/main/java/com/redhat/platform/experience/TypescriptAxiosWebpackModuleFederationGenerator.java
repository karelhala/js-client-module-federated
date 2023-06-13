package com.redhat.platform.experience;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.model.*;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;

import java.util.*;
import java.io.File;

public class TypescriptAxiosWebpackModuleFederationGenerator extends DefaultCodegen implements CodegenConfig {

  // source folder where to write the files
  protected String sourceFolder = "src";
  protected String apiVersion = "1.0.0";

  /**
   * Configures the type of generator.
   *
   * @return  the CodegenType for this generator
   * @see     org.openapitools.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.OTHER;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the generator
   * to select the library with the -g flag.
   *
   * @return the friendly name for the generator
   */
  public String getName() {
    return "typescript-axios-webpack-module-federation";
  }

  /**
   * Provides an opportunity to inspect and modify operation data before the code is generated.
   */
  @Override
  public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {

    // to try debugging your code generator:
    // set a break point on the next line.
    // then debug the JUnit test called LaunchGeneratorInDebugger

    OperationsMap results = super.postProcessOperationsWithModels(objs, allModels);

    OperationMap ops = results.getOperations();
    List<CodegenOperation> opList = ops.getOperation();

    // iterate over the operation and perhaps modify something
    for(CodegenOperation co : opList){
      // example:
      // co.httpMethod = co.httpMethod.toLowerCase();
    }

    return results;
  }

  /**
   * Returns human-friendly help for the generator.  Provide the consumer with help
   * tips, parameters here
   *
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a typescript-axios-webpack-module-federation client library.";
  }

  public TypescriptAxiosWebpackModuleFederationGenerator() {
    super();

    // set the output folder here
    outputFolder = "generated-code/typescript-axios-webpack-module-federation";

    /**
     * Models.  You can write model files using the modelTemplateFiles map.
     * if you want to create one template for file, you can do so here.
     * for multiple files for model, just put another entry in the `modelTemplateFiles` with
     * a different extension
     */
    modelTemplateFiles.put(
      "model.mustache", // the template to use
      ".ts");       // the extension for each file to write

    /**
     * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
     * as with models, add multiple entries with different extensions for multiple files per
     * class
     */
    apiTemplateFiles.put(
      "api.mustache",   // the template to use
      "index.ts");       // the extension for each file to write
    apiTemplateFiles.put(
      "package.mustache",   // the template to use
      "package.sample");       // the extension for each file to write

    /**
     * Template Location.  This is the location which templates will be read from.  The generator
     * will use the resource stream to attempt to read the templates.
     */
    templateDir = "typescript-axios-webpack-module-federation";

    /**
     * Reserved words.  Override this with reserved words specific to your language
     */
    reservedWords = new HashSet<String> (
      Arrays.asList(
        "sample1",  // replace with static values
        "sample2")
    );

    /**
     * Additional Properties.  These values can be passed to the templates and
     * are available in models, apis, and supporting files
     */
    additionalProperties.put("apiVersion", apiVersion);

    /**
     * Supporting Files.  You can write single files for the generator with the
     * entire object tree available.  If the input file has a suffix of `.mustache
     * it will be processed by the template engine.  Otherwise, it will be copied
     */
    supportingFiles.add(new SupportingFile("index.mustache",   // the input template or file
      "",                                                       // the destination folder, relative `outputFolder`
      "index.ts")                                          // the output file
    );

    /**
     * Language Specific Primitives.  These types will not trigger imports by
     * the client generator
     */
    languageSpecificPrimitives = new HashSet<String>(
      Arrays.asList(
        "Type1",      // replace these with your types
        "Type2")
    );
  }

  /**
   * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
   * those terms here.  This logic is only called if a variable matches the reserved words
   *
   * @return the escaped term
   */
  @Override
  public String escapeReservedWord(String name) {
    return "_" + name;  // add an underscore to the name
  }

  @Override
  public String toModelFilename(String name) {
      return "models" + File.separator + name + File.separator + "index";
  }

  @Override
  public String toApiFilename(String name) {
      return name + File.separator;
  }

  /**
     * Get operationId from the operation object, and if it's blank, generate a new one from the given parameters.
     *
     * @param operation  the operation object
     * @param path       the path of the operation
     * @param httpMethod the HTTP method of the operation
     * @return the (generated) operationId
     */
    protected String getOrGenerateOperationId(Operation operation, String path, String httpMethod) {
      String operationId = operation.getOperationId();
      if (StringUtils.isBlank(operationId)) {
          String tmpPath = path;
          tmpPath = tmpPath.replaceAll("\\{", "");
          tmpPath = tmpPath.replaceAll("\\}", "");
          String[] parts = (tmpPath + "/" + httpMethod).split("/");
          StringBuilder builder = new StringBuilder();
          if ("/".equals(tmpPath)) {
              // must be root tmpPath
              builder.append("root");
          }
          for (String part : parts) {
              if (part.length() > 0) {
                  if (builder.toString().length() == 0) {
                      part = Character.toLowerCase(part.charAt(0)) + part.substring(1);
                  } else {
                      part = CaseUtils.toCamelCase(part, true);
                  }
                  builder.append(part);
              }
          }
          operationId = sanitizeName(builder.toString());
      }
      return operationId;
  }

  @Override
  public void preprocessOpenAPI(OpenAPI openAPI) {
      Map<String, PathItem> pathItems = openAPI.getPaths();
      if (pathItems != null) {
        for (Map.Entry<String, PathItem> e : pathItems.entrySet()) {
          for (Map.Entry<PathItem.HttpMethod, Operation> op : e.getValue().readOperationsMap().entrySet()) {
            System.out.println("=========== This is tags! " + op.getValue().getTags());
            System.out.println("=========== This is key! " + op.getKey());
            List<String> tags = new ArrayList<String>();
            tags.add(getOrGenerateOperationId(op.getValue(), e.getKey(), op.getKey().toString()));
            op.getValue().setTags(tags);
          }
        }
      }
      super.preprocessOpenAPI(openAPI);
  }

  /**
   * override with any special text escaping logic to handle unsafe
   * characters so as to avoid code injection
   *
   * @param input String to be cleaned up
   * @return string with unsafe characters removed or escaped
   */
  @Override
  public String escapeUnsafeCharacters(String input) {
    //TODO: check that this logic is safe to escape unsafe characters to avoid code injection
    return input;
  }

  /**
   * Escape single and/or double quote to avoid code injection
   *
   * @param input String to be cleaned up
   * @return string with quotation mark removed or escaped
   */
  public String escapeQuotationMark(String input) {
    //TODO: check that this logic is safe to escape quotation mark to avoid code injection
    return input.replace("\"", "\\\"");
  }
}
