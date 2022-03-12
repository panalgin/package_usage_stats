#import "PackageUsageStatsPlugin.h"
#if __has_include(<package_usage_stats/package_usage_stats-Swift.h>)
#import <package_usage_stats/package_usage_stats-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "package_usage_stats-Swift.h"
#endif

@implementation PackageUsageStatsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPackageUsageStatsPlugin registerWithRegistrar:registrar];
}
@end
